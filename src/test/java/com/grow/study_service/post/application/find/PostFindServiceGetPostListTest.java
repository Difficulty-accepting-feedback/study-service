package com.grow.study_service.post.application.find;

import com.grow.study_service.board.domain.enums.BoardType;
import com.grow.study_service.board.domain.model.Board;
import com.grow.study_service.board.domain.repository.BoardRepository;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.groupmember.domain.enums.Role;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.PostRepository;
import com.grow.study_service.post.presentation.dto.response.PostSimpleResponse;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class PostFindServiceGetPostListTest {

    @Autowired
    private PostFindService postFindService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private BoardRepository boardRepository;

    private static final Long BOARD_ID = 100L;
    private static final Long MEMBER_ID = 10L;

    private Long seedBoard(String name) {
        // Board.create(groupId, boardType, name, description)
        Board board = Board.create(1L, BoardType.DATA_SHARING, name, "과제 제출 게시판입니다.");
        Board saved = boardRepository.save(board);
        return saved.getBoardId();
    }

    private void seedMembership(Long boardId, Long memberId) {
        GroupMember gm = new GroupMember(
                null,
                boardId,
                memberId,
                Role.MEMBER,
                LocalDateTime.now()
        );
        groupMemberRepository.save(gm);
    }

    private void seedPosts(Long boardId, Long authorMemberId, int count) {
        for (int i = 1; i <= count; i++) {
            Post post = Post.create(boardId, authorMemberId, "제목" + i, "내용" + i);
            postRepository.save(post);
        }
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("그룹 멤버는 해당 보드의 게시글 목록을 조회할 수 있다")
        void getPostList_success_withPosts() {
            // given
            Long boardId = seedBoard("자료 공유");
            seedMembership(boardId, MEMBER_ID);
            seedPosts(boardId, MEMBER_ID, 3);

            // when
            List<PostSimpleResponse> results = postFindService.getPostList(MEMBER_ID, boardId);

            // then
            assertThat(results).isNotNull();
            assertThat(results).hasSize(3);

            assertThat(results)
                    .extracting("title")
                    .containsExactlyInAnyOrder("제목1", "제목2", "제목3");

            assertThat(results)
                    .extracting("postId")
                    .allSatisfy(id -> assertThat(id).isNotNull());
        }

        @Test
        @DisplayName("게시글이 하나도 없어도 빈 목록을 반환한다")
        void getPostList_success_empty() {
            // given
            Long boardId = seedBoard("QnA");
            seedMembership(boardId, MEMBER_ID);
            // 게시글 없음

            // when
            List<PostSimpleResponse> results = postFindService.getPostList(MEMBER_ID, boardId);

            // then
            assertThat(results).isNotNull();
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("그룹 멤버가 아니면 권한 예외 발생")
        void getPostList_forbidden() {
            // given
            // 멤버십 미부여
            Long boardId = seedBoard("공유 게시판");
            seedPosts(BOARD_ID, MEMBER_ID, 2);

            // when & then
            assertThatThrownBy(() -> postFindService.getPostList(MEMBER_ID, boardId))
                    .isInstanceOf(ServiceException.class);
        }

        @Test
        @DisplayName("존재하지 않는 보드에 대해 조회 시 예외(또는 빈 목록)")
        void getPostList_boardNotFound_orEmpty() {
            // given
            Long notExistBoardId = 99999L;
            seedMembership(notExistBoardId, MEMBER_ID);

            // when & then
            assertThatThrownBy(() -> postFindService.getPostList(MEMBER_ID, notExistBoardId))
                     .isInstanceOf(ServiceException.class);
        }
    }
}
