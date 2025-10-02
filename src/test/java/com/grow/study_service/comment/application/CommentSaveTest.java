package com.grow.study_service.comment.application;

import com.grow.study_service.board.domain.enums.BoardType;
import com.grow.study_service.board.infra.entity.BoardJpaEntity;
import com.grow.study_service.board.infra.repository.BoardJpaRepository;
import com.grow.study_service.comment.domain.repository.CommentRepository;
import com.grow.study_service.comment.presentation.dto.CommentResponse;
import com.grow.study_service.comment.presentation.dto.CommentSaveRequest;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CommentService#save 통합 테스트")
public class CommentSaveTest {

    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    GroupJpaRepository groupJpaRepository;
    @Autowired
    BoardJpaRepository boardRepository;
    @Autowired
    PostJpaRepository postRepository;
    @Autowired
    GroupMemberJpaRepository groupMemberRepository;

    /* ───────────────── 테스트 고정 데이터 ───────────────── */
    private GroupJpaEntity group;
    private BoardJpaEntity board;
    private PostJpaEntity post;

    private static final long MEMBER_ID   = 100L;    // 정상 회원
    private static final long OTHER_ID    = 200L;    // 그룹 미가입 회원
    private static final String CONTENT   = "hello world";

    @BeforeEach
    void setUp() {
        /* 1. 그룹 생성 */
        group = groupJpaRepository.save(
                GroupJpaEntity.builder()
                        .name("스터디그룹")
                        .category(Category.STUDY)
                        .description("설명")
                        .startAt(LocalDate.now())
                        .endAt(LocalDate.now().plusYears(1))
                        .build()
        );

        /* 2. 보드 생성 */
        board = boardRepository.save(
                BoardJpaEntity.builder()
                        .groupId(group.getId())
                        .name("게시판")
                        .description("desc")
                        .boardType(BoardType.ASSIGNMENT_SUBMISSION)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        /* 3. 게시글 생성 */
        post = postRepository.save(
                PostJpaEntity.builder()
                        .boardId(board.getId())
                        .memberId(MEMBER_ID)
                        .title("제목")
                        .content("본문")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        /* 4. 그룹 멤버 등록 (MEMBER_ID 만) */
        groupMemberRepository.save(
                GroupMemberJpaEntity.builder()
                        .memberId(MEMBER_ID)
                        .groupId(group.getId())
                        .role(Role.MEMBER)
                        .joinedAt(LocalDateTime.now())
                        .build()
        );
    }

    @Nested
    @DisplayName("성공 케이스")
    class Success {

        @Test
        @DisplayName("그룹 멤버가 중복 아닌 댓글을 작성하면 저장된다")
        void save_comment_successfully() {
            CommentSaveRequest req = CommentSaveRequest.builder()
                    .parentId(null)
                    .content(CONTENT)
                    .build();

            CommentResponse res = commentService.save(MEMBER_ID, post.getId(), req);

            assertThat(res.getCommentId()).isNotNull();
            assertThat(res.getContent()).isEqualTo(CONTENT);
            assertThat(commentRepository.existsByPostIdAndMemberIdAndContent(
                    post.getId(), MEMBER_ID, CONTENT)
            ).isTrue();
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class Failure {

        @Test
        @DisplayName("그룹 미가입 회원이 댓글을 저장하면 INVALID_POST_ACCESS 예외 발생")
        void save_comment_invalid_access() {
            CommentSaveRequest req = CommentSaveRequest.builder()
                    .parentId(null)
                    .content(CONTENT)
                    .build();

            ServiceException ex = assertThrows(ServiceException.class,
                    () -> commentService.save(OTHER_ID, post.getId(), req));

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_POST_ACCESS);
        }

        @Test
        @DisplayName("같은 회원이 동일한 내용의 댓글을 두 번 저장하면 COMMENT_ALREADY_EXISTS 예외 발생")
        void save_duplicate_comment() {
            CommentSaveRequest req = CommentSaveRequest.builder()
                    .parentId(null)
                    .content(CONTENT)
                    .build();

            /* 첫 번째 저장 → 성공 */
            commentService.save(MEMBER_ID, post.getId(), req);

            /* 두 번째 저장 → 실패 */
            ServiceException ex = assertThrows(ServiceException.class,
                    () -> commentService.save(MEMBER_ID, post.getId(), req));

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.COMMENT_ALREADY_EXISTS);
        }
    }
}
