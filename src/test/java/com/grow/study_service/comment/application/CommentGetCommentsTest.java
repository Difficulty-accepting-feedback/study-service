package com.grow.study_service.comment.application;

import com.grow.study_service.board.domain.enums.BoardType;
import com.grow.study_service.board.infra.entity.BoardJpaEntity;
import com.grow.study_service.board.infra.repository.BoardJpaRepository;
import com.grow.study_service.comment.domain.model.Comment;
import com.grow.study_service.comment.domain.repository.CommentRepository;
import com.grow.study_service.comment.presentation.dto.CommentResponse;
import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.infra.persistence.entity.GroupJpaEntity;
import com.grow.study_service.group.infra.persistence.repository.GroupJpaRepository;
import com.grow.study_service.groupmember.domain.enums.Role;
import com.grow.study_service.groupmember.infra.persistence.entity.GroupMemberJpaEntity;
import com.grow.study_service.groupmember.infra.persistence.repository.GroupMemberJpaRepository;
import com.grow.study_service.post.infra.persistence.entity.PostJpaEntity;
import com.grow.study_service.post.infra.persistence.repository.PostJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CommentService#getCommentsByPostId 통합 테스트 (권한 검증 포함)")
public class CommentGetCommentsTest {
    /*────────────────── 고정 값 ──────────────────*/
    private static final long MEMBER_ID      = 10L;   // 그룹 가입 회원
    private static final long NON_MEMBER_ID  = 99L;   // 그룹 미가입 회원
    private static final String ROOT_TXT     = "root";

    /*────────────────── DI ──────────────────*/
    @Autowired
    CommentService        commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    GroupJpaRepository groupRepository;
    @Autowired
    BoardJpaRepository boardRepository;
    @Autowired
    PostJpaRepository postRepository;
    @Autowired
    GroupMemberJpaRepository groupMemberRepository;

    /*────────────────── 공용 픽스처 ──────────────────*/
    private GroupJpaEntity group;
    private BoardJpaEntity board;

    @BeforeEach
    void outerSetUp() {
        /* 그룹, 게시판, 그룹 멤버( MEMBER_ID ) 세팅 */
        group = groupRepository.save(
                GroupJpaEntity.builder()
                        .name("스터디")
                        .category(Category.STUDY)
                        .description("desc")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        board = boardRepository.save(
                BoardJpaEntity.builder()
                        .groupId(group.getId())
                        .name("게시판")
                        .description("desc")
                        .boardType(BoardType.ASSIGNMENT_SUBMISSION) // 과제 제출 게시판
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        groupMemberRepository.save(
                GroupMemberJpaEntity.builder()
                        .memberId(MEMBER_ID)
                        .groupId(group.getId())
                        .role(Role.MEMBER)
                        .joinedAt(LocalDateTime.now())
                        .build()
        );
    }

    /*──────────────────── 성공 케이스 ────────────────────*/
    @Nested
    @DisplayName("성공 케이스")
    class Success {

        /* root ─ child */
        @Nested
        @DisplayName("대댓글이 1개일 때")
        class SingleReply {

            private long postId;

            @BeforeEach
            void setUp() {
                postId = createPost().getId();

                Comment root = commentRepository.save(
                        Comment.create(postId, MEMBER_ID, null, ROOT_TXT, LocalDateTime.now())
                );
                commentRepository.save(
                        Comment.create(postId, MEMBER_ID, root.getCommentId(), "child", LocalDateTime.now())
                );
            }

            @Test
            @DisplayName("루트 댓글 1개, 대댓글 1개가 트리로 반환된다")
            void returns_single_child() {
                List<CommentResponse> result = commentService.getCommentsByPostId(postId, MEMBER_ID);

                assertThat(result).hasSize(1)
                        .first()
                        .satisfies(r -> {
                            assertThat(r.getReplies()).hasSize(1);
                            assertThat(r.getReplies().get(0).getContent()).isEqualTo("child");
                        });
            }
        }

        /* root ─ child-1, child-2, child-3 */
        @Nested
        @DisplayName("대댓글이 여러 개일 때")
        class MultipleReplies {

            private long postId;

            @BeforeEach
            void setUp() {
                postId = createPost().getId();
                Comment root = commentRepository.save(
                        Comment.create(postId, MEMBER_ID, null, ROOT_TXT, LocalDateTime.now())
                );

                commentRepository.save(
                        Comment.create(postId, MEMBER_ID, root.getCommentId(), "child-1", LocalDateTime.now())
                );
                commentRepository.save(
                        Comment.create(postId, MEMBER_ID, root.getCommentId(), "child-2", LocalDateTime.now())
                );
                commentRepository.save(
                        Comment.create(postId, MEMBER_ID, root.getCommentId(), "child-3", LocalDateTime.now())
                );
            }

            @Test
            @DisplayName("루트 댓글과 3개의 대댓글이 계층 구조로 반환된다")
            void returns_three_children() {
                List<CommentResponse> result = commentService.getCommentsByPostId(postId, MEMBER_ID);

                assertThat(result).hasSize(1);
                assertThat(result.get(0).getReplies())
                        .hasSize(3)
                        .extracting(CommentResponse::getContent)
                        .containsExactlyInAnyOrder("child-1", "child-2", "child-3");
            }
        }

        /* root → child → grandChild → greatGrandChild */
        @Nested
        @DisplayName("깊이 4 이상의 무한 계층")
        class MultiDepthReplies {

            private long postId;

            @BeforeEach
            void setUp() {
                postId = createPost().getId();
                Comment root = commentRepository.save(
                        Comment.create(postId, MEMBER_ID, null, ROOT_TXT, LocalDateTime.now())
                );
                Comment child = commentRepository.save(
                        Comment.create(postId, MEMBER_ID, root.getCommentId(), "child", LocalDateTime.now())
                );
                Comment grandChild = commentRepository.save(
                        Comment.create(postId, MEMBER_ID, child.getCommentId(), "grandChild", LocalDateTime.now())
                );
                commentRepository.save(
                        Comment.create(postId, MEMBER_ID, grandChild.getCommentId(), "greatGrandChild", LocalDateTime.now())
                );
            }

            @Test
            @DisplayName("4단계 트리를 정확히 반환한다")
            void returns_deep_tree() {
                List<CommentResponse> res = commentService.getCommentsByPostId(postId, MEMBER_ID);

                CommentResponse root = res.get(0);
                CommentResponse child = root.getReplies().get(0);
                CommentResponse grand = child.getReplies().get(0);

                assertThat(grand.getReplies())
                        .singleElement()
                        .extracting(CommentResponse::getContent)
                        .isEqualTo("greatGrandChild");
            }
        }

        /* 댓글이 아예 없는 게시글 */
        @Nested
        @DisplayName("댓글이 없는 게시글")
        class EmptyPost {

            private long postId;

            @BeforeEach
            void setUp() { postId = createPost().getId(); }

            @Test
            @DisplayName("빈 리스트를 반환한다")
            void returns_empty() {
                List<CommentResponse> result = commentService.getCommentsByPostId(postId, MEMBER_ID);
                assertThat(result).isEmpty();
            }
        }
    }

    /*──────────────────── 실패 케이스 ────────────────────*/
    @Nested
    @DisplayName("실패·예외 케이스")
    class Failure {

        @Test
        @DisplayName("그룹 미가입 회원이 조회하면 INVALID_POST_ACCESS 예외 발생")
        void invalid_access() {
            long postId = createPost().getId();

            ServiceException ex = assertThrows(ServiceException.class,
                    () -> commentService.getCommentsByPostId(postId, NON_MEMBER_ID));

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_POST_ACCESS);
        }
    }

    /*──────────────────── 헬퍼 메서드 ────────────────────*/
    private PostJpaEntity createPost() {
        return postRepository.save(
                PostJpaEntity.builder()
                        .boardId(board.getId())
                        .memberId(MEMBER_ID)
                        .title("제목")
                        .content("본문")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}
