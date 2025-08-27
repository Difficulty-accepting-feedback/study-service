package com.grow.study_service.comment.application;

import com.grow.study_service.board.domain.enums.BoardType;
import com.grow.study_service.board.domain.model.Board;
import com.grow.study_service.board.domain.repository.BoardRepository;
import com.grow.study_service.comment.domain.model.Comment;
import com.grow.study_service.comment.domain.repository.CommentRepository;
import com.grow.study_service.comment.presentation.dto.CommentResponse;
import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.group.domain.repository.GroupRepository;
import com.grow.study_service.groupmember.domain.enums.Role;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.PostRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("CommentService#delete 통합 테스트")
public class DeleteCommentTest {

    /* 고정 값 */
    private static final long AUTHOR_ID = 10L;
    private static final long OTHER_ID = 20L;

    /* DI */
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    GroupMemberRepository groupMemberRepository;

    /* 공용 픽스처 */
    private Group group;
    private Board board;
    private Long boardId;

    @BeforeEach
    void outerSetUp() {
        /* ① 그룹 도메인 객체 생성 및 저장 */
        group = groupRepository.save(Group.create("스터디 그룹", Category.STUDY, "도메인 테스트용 그룹"));

        /* ② 게시판 도메인 객체 생성 및 저장 */
        board = Board.create(
                group.getGroupId(),
                BoardType.ASSIGNMENT_SUBMISSION,
                "과제 제출 게시판",
                "도메인 레이어 테스트용 게시판"
        );
        Board saved = boardRepository.save(board);
        boardId = saved.getBoardId();

        /* ③ 그룹 멤버(AUTHOR_ID) 저장 */
        GroupMember member = GroupMember.create(
                AUTHOR_ID,
                group.getGroupId(),
                Role.MEMBER
        );
        groupMemberRepository.save(member);
    }

    /* ─────────── 성공 케이스 ─────────── */
    @Nested
    @DisplayName("성공 케이스")
    class Success {

        @Nested
        @DisplayName("단독 댓글 삭제")
        class DeleteSingleComment {

            private long postId;
            private long commentId;

            @BeforeEach
            void setUp() {
                /* 1) 게시글(Post) 도메인 객체 저장 */
                Post post = Post.create(
                        boardId,
                        AUTHOR_ID,
                        "도메인 테스트 게시글",
                        "본문"
                );
                postId = postRepository.save(post).getPostId();

                /* 2) 댓글(Comment) 도메인 객체 저장 */
                Comment comment = Comment.create(
                        postId,
                        AUTHOR_ID,
                        null,
                        "삭제 대상 댓글",
                        LocalDateTime.now().minusMinutes(1)
                );
                commentId = commentRepository.save(comment).getCommentId();
            }

            @Test
            @DisplayName("soft delete 적용 → isDeleted=true, updatedAt 갱신, 응답에 \"삭제되었습니다\" 표시")
            void delete_comment_success() {
                log.info("boardId={}", boardId);
                commentService.deleteComment(commentId, postId, AUTHOR_ID);

                /* ASSERT 1: 도메인 객체 조회 후 softDelete 플래그 & updatedAt 검증 */
                Comment deleted = commentRepository.findById(commentId).orElseThrow();
                assertThat(deleted.isDeleted()).isTrue();
                assertThat(deleted.getUpdatedAt()).isAfter(deleted.getCreatedAt());

                /* ASSERT 2: 계층형 조회 결과 확인 */
                List<CommentResponse> tree = commentService.getCommentsByPostId(postId, AUTHOR_ID);
                assertThat(tree).hasSize(1);
                assertThat(tree.get(0).getContent()).isEqualTo("삭제되었습니다.");
            }
        }

        /* 계층 구조 유지 (루트 삭제 시 자식 유지) */
        @Nested
        @DisplayName("루트 댓글 삭제 시 자식 유지")
        class RootWithChildren {

            private long postId;
            private long rootId;

            @BeforeEach
            void setUp() {
                Post post = Post.create(
                        boardId,
                        AUTHOR_ID,
                        "도메인 테스트 게시글",
                        "본문"
                );
                postId = postRepository.save(post).getPostId();

                Comment root = commentRepository.save(
                        Comment.create(postId, AUTHOR_ID, null, "루트", LocalDateTime.now())
                );
                rootId = root.getCommentId();
                commentRepository.save(
                        Comment.create(postId, AUTHOR_ID, rootId, "대댓글1", LocalDateTime.now())
                );
                commentRepository.save(
                        Comment.create(postId, AUTHOR_ID, rootId, "대댓글2", LocalDateTime.now())
                );
            }

            @Test
            @DisplayName("루트 삭제 후 '삭제되었습니다' 표시, 대댓글 2개 유지")
            void delete_root_keeps_children() {
                commentService.deleteComment(rootId, postId, AUTHOR_ID);

                List<CommentResponse> result = commentService.getCommentsByPostId(postId, AUTHOR_ID);
                assertThat(result).hasSize(1);
                CommentResponse deletedRoot = result.get(0);

                assertThat(deletedRoot.getContent()).isEqualTo("삭제되었습니다.");
                assertThat(deletedRoot.getReplies()).hasSize(2)
                        .extracting(CommentResponse::getContent)
                        .containsExactlyInAnyOrder("대댓글1", "대댓글2");
            }
        }

        /* 무한 계층 (중간 삭제 시 구조 유지) */
        @Nested
        @DisplayName("무한 계층 중 중간 삭제")
        class MultiDepth {

            private long postId;
            private long childId;  // 삭제할 중간 댓글

            @BeforeEach
            void setUp() {
                Post post = Post.create(
                        boardId,
                        AUTHOR_ID,
                        "도메인 테스트 게시글",
                        "본문"
                );
                postId = postRepository.save(post).getPostId();

                Comment root = commentRepository.save(
                        Comment.create(postId, AUTHOR_ID, null, "루트", LocalDateTime.now())
                );
                Comment child = commentRepository.save(
                        Comment.create(postId, AUTHOR_ID, root.getCommentId(), "대댓글", LocalDateTime.now())
                );
                childId = child.getCommentId();
                Comment grand = commentRepository.save(
                        Comment.create(postId, AUTHOR_ID, childId, "대대댓글", LocalDateTime.now())
                );
                commentRepository.save(
                        Comment.create(postId, AUTHOR_ID, grand.getCommentId(), "대대대댓글", LocalDateTime.now())
                );
            }

            @Test
            @DisplayName("중간(대댓글) 삭제 후 구조 유지, 자식(대대댓글 등) '삭제되었습니다' 아래로 이동")
            void delete_middle_keeps_structure() {
                commentService.deleteComment(childId, postId, AUTHOR_ID);

                List<CommentResponse> result = commentService.getCommentsByPostId(postId, AUTHOR_ID);
                CommentResponse root = result.get(0);
                CommentResponse deletedChild = root.getReplies().get(0);

                assertThat(deletedChild.getContent()).isEqualTo("삭제되었습니다.");
                assertThat(deletedChild.getReplies()).hasSize(1);
                CommentResponse grand = deletedChild.getReplies().get(0);

                assertThat(grand.getContent()).isEqualTo("대대댓글");
                assertThat(grand.getReplies()).hasSize(1)
                        .first()
                        .extracting(CommentResponse::getContent)
                        .isEqualTo("대대대댓글");
            }
        }
    }

    /* ─────────────────── 실패 케이스 ─────────────────── */
    @Nested
    @DisplayName("실패 케이스")
    class Failure {
        @Nested
        @DisplayName("다른 사용자의 댓글 삭제 시도")
        class DeleteOtherComment {

            private long postId;
            private long commentId;

            @BeforeEach
            void setUp() {
                /* 1) 게시글(Post) 도메인 객체 저장 */
                Post post = Post.create(
                        boardId,
                        AUTHOR_ID,
                        "도메인 테스트 게시글",
                        "본문"
                );
                postId = postRepository.save(post).getPostId();

                /* 2) 다른 사용자의 그룹 멤버(OTHER_ID) 저장 */
                GroupMember otherMember = GroupMember.create(
                        OTHER_ID,
                        group.getGroupId(),
                        Role.MEMBER
                );
                groupMemberRepository.save(otherMember);

                /* 3) AUTHOR_ID의 댓글(Comment) 도메인 객체 저장 */
                Comment comment = Comment.create(
                        postId,
                        AUTHOR_ID,  // AUTHOR_ID가 작성자
                        null,
                        "다른 사용자가 삭제 시도할 댓글",
                        LocalDateTime.now().minusMinutes(1)
                );
                commentId = commentRepository.save(comment).getCommentId();
            }

            @Test
            @DisplayName("자기 댓글이 아니면 DomainException(INVALID_COMMENT_ACCESS) 발생")
            void delete_other_comment_failure() {
                // OTHER_ID로 삭제 시도
                assertThatThrownBy(() -> commentService.deleteComment(commentId, postId, OTHER_ID))
                        .isInstanceOf(DomainException.class);

                // 삭제되지 않았는지 확인 (isDeleted=false 유지)
                Comment unchanged = commentRepository.findById(commentId).orElseThrow();
                assertThat(unchanged.isDeleted()).isFalse();
                assertThat(unchanged.getUpdatedAt()).isEqualTo(unchanged.getCreatedAt());
            }
        }
    }
}
