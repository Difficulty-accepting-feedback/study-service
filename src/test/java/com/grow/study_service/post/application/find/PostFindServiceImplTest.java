package com.grow.study_service.post.application.find;

import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.groupmember.domain.enums.Role;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import com.grow.study_service.post.domain.model.FileMeta;
import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.FileMetaRepository;
import com.grow.study_service.post.domain.repository.PostRepository;
import com.grow.study_service.post.presentation.dto.response.PostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PostFindServiceImplTest {

    @Autowired
    private PostFindService postFindService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private FileMetaRepository fileMetaRepository;

    private Long seedPostWithFilesAndMembership() {
        // Given: 게시글 생성
        Post post = Post.create(100L, 10L, "제목", "내용");
        Post savedPost = postRepository.save(post);

        // 멤버십(권한) 부여: memberId=10이 boardId=100에 속해 있다고 가정
        GroupMember gm = GroupMember.create(100L, 10L, Role.MEMBER);
        groupMemberRepository.save(gm);

        // 파일 2개 저장
        FileMeta f1 = FileMeta.create(savedPost.getPostId(), "a.png", "uuid-a.png", "image/png", 1234L, "/files/uuid-a.png");
        FileMeta f2 = FileMeta.create(savedPost.getPostId(), "b.pdf", "uuid-b.pdf", "application/pdf", 9999L, "/files/uuid-b.pdf");
        fileMetaRepository.save(f1);
        fileMetaRepository.save(f2);

        return savedPost.getPostId();
    }

    @Nested
    class SuccessCases {

        @Test
        @DisplayName("정상적으로 게시글 단건 조회 및 파일 메타 정보 포함 반환")
        void findById_success() {
            // given
            Long postId = seedPostWithFilesAndMembership();
            Long requesterMemberId = 10L;

            // when
            PostResponse response = postFindService.findById(postId, requesterMemberId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getPostId()).isEqualTo(postId);
            assertThat(response.getTitle()).isEqualTo("제목");
            assertThat(response.getContent()).isEqualTo("내용");

            // 파일 정보가 매핑되어 들어오는지 검증
            assertThat(response.getFiles()).isNotNull();
            assertThat(response.getFiles()).hasSize(2);
            assertThat(response.getFiles())
                    .extracting("originalName")
                    .containsExactlyInAnyOrder("a.png", "b.pdf");
        }
    }

    @Nested
    class FailureCases {

        @Test
        @DisplayName("존재하지 않는 게시글이면 예외 발생")
        void findById_postNotFound() {
            // given
            Long notExistPostId = 999999L;
            Long requesterMemberId = 10L;

            // when & then
            assertThatThrownBy(() -> postFindService.findById(notExistPostId, requesterMemberId))
                    .isInstanceOf(ServiceException.class);
        }

        @Test
        @DisplayName("권한 없는 멤버가 조회 시 예외 발생")
        void findById_noPermission() {
            // given: 게시글은 생성되지만 멤버십은 부여하지 않음
            Post post = Post.create(200L, 20L, "비공개제목", "비공개내용");
            Long savedPostId = postRepository.save(post).getPostId();

            Long otherMemberId = 999L; // 그룹 미가입자

            // when & then
            assertThatThrownBy(() -> postFindService.findById(savedPostId, otherMemberId))
                    .isInstanceOf(ServiceException.class);
        }
    }
}