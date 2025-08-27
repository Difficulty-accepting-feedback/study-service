package com.grow.study_service.comment.application;

import com.grow.study_service.board.domain.enums.BoardType;
import com.grow.study_service.board.infra.entity.BoardJpaEntity;
import com.grow.study_service.board.infra.repository.BoardJpaRepository;
import com.grow.study_service.comment.domain.model.Comment;
import com.grow.study_service.comment.domain.repository.CommentRepository;
import com.grow.study_service.comment.presentation.dto.CommentResponse;
import com.grow.study_service.comment.presentation.dto.CommentSaveRequest;
import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CommentService#updateComment 통합 테스트")
public class UpdateCommentTest {

    /* 고정 값 */
    private static final long AUTHOR_ID    = 10L;
    private static final long OTHER_ID     = 20L;
    private static final String ORIGINAL   = "원본 댓글";
    private static final String UPDATED    = "수정된 댓글";

    /* DI */
    @Autowired
    CommentService commentService;
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

    /* 공용 픽스처 */
    private long postId;
    private long commentId;

    @BeforeEach
    void setUp() {
        /* 그룹·보드·멤버·게시글 준비 */
        GroupJpaEntity group = groupRepository.save(
                GroupJpaEntity.builder()
                        .name("스터디")
                        .category(Category.STUDY)
                        .description("desc")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        BoardJpaEntity board = boardRepository.save(
                BoardJpaEntity.builder()
                        .groupId(group.getId())
                        .name("게시판")
                        .description("desc")
                        .boardType(BoardType.ASSIGNMENT_SUBMISSION)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        postId = postRepository.save(
                PostJpaEntity.builder()
                        .boardId(board.getId())
                        .memberId(AUTHOR_ID)
                        .title("제목")
                        .content("본문")
                        .createdAt(LocalDateTime.now())
                        .build()
        ).getId();

        /* AUTHOR_ID만 그룹 가입 */
        groupMemberRepository.save(
                GroupMemberJpaEntity.builder()
                        .memberId(AUTHOR_ID)
                        .groupId(group.getId())
                        .role(Role.MEMBER)
                        .joinedAt(LocalDateTime.now())
                        .build()
        );

        groupMemberRepository.save(
                GroupMemberJpaEntity.builder()
                        .memberId(OTHER_ID)
                        .groupId(group.getId())
                        .role(Role.MEMBER)
                        .joinedAt(LocalDateTime.now())
                        .build()
        );

        /* 원본 댓글 저장 */
        commentId = commentRepository.save(
                Comment.create(postId, AUTHOR_ID, null, ORIGINAL, LocalDateTime.now())
        ).getCommentId();
    }

    /* ─────────── 성공 케이스 ─────────── */
    @Nested
    @DisplayName("성공 케이스")
    class Success {

        @Test
        @DisplayName("작성자가 자신의 댓글을 수정하면 내용과 updatedAt 필드가 변경된다")
        void update_comment_success() {
            CommentSaveRequest req = CommentSaveRequest.builder()
                    .content(UPDATED)
                    .parentId(null)
                    .build();

            CommentResponse res = commentService.updateComment(AUTHOR_ID, postId, commentId, req);

            assertThat(res.getContent()).isEqualTo(UPDATED);
            assertThat(res.getCreatedAt()).isNotNull();
            assertThat(res.getUpdatedAt()).isNotNull();           // 수정 시점 반영
            assertThat(res.getUpdatedAt()).isAfter(res.getCreatedAt());
        }
    }

    /* ─────────── 실패 케이스 ─────────── */
    @Nested
    @DisplayName("실패·예외 케이스")
    class Failure {

        @Test
        @DisplayName("작성자가 아닌 사용자가 수정하면 INVALID_COMMENT_ACCESS 예외가 발생한다")
        void update_comment_invalid_user() {
            CommentSaveRequest req = CommentSaveRequest.builder()
                    .content(UPDATED)
                    .build();

            DomainException ex = assertThrows(DomainException.class,
                    () -> commentService.updateComment(OTHER_ID, postId, commentId, req));

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_COMMENT_ACCESS);
        }

        @Test
        @DisplayName("존재하지 않는 commentId 수정 시 COMMENT_NOT_FOUND 예외가 발생한다")
        void update_comment_not_found() {
            CommentSaveRequest req = CommentSaveRequest.builder()
                    .content(UPDATED)
                    .build();

            long invalidCommentId = 9999L;

            ServiceException ex = assertThrows(ServiceException.class,
                    () -> commentService.updateComment(AUTHOR_ID, postId, invalidCommentId, req));

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
        }
    }
}
