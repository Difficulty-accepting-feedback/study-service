package com.grow.study_service.post.application.delete;

import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.post.application.file.delete.FileDeleteService;
import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PostDeleteServiceImplTest {

    @Autowired
    private PostDeleteService postDeleteService;

    @Autowired
    private PostRepository postRepository;

    @MockitoBean
    private FileDeleteService fileDeleteService;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("본인 게시물 삭제 성공 - DB에서 삭제되고 파일 삭제 호출됨")
        void deletePost_success() {
            // Given: 테스트 게시물 생성 및 저장
            Long memberId = 1L;
            Long boardId = 1L;
            Post post = Post.create(boardId, memberId, "Test Title", "Test Content");
            Post savedPost = postRepository.save(post);
            Long postId = savedPost.getPostId();

            // When: 삭제 메서드 호출
            postDeleteService.deletePost(memberId, postId);

            // Then: DB에서 게시물이 삭제되었는지 확인
            assertThat(postRepository.findById(postId)).isEmpty();

            // 파일 삭제 메서드가 호출되었는지 확인
            verify(fileDeleteService).deleteFilesForPost(eq(postId));
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("존재하지 않는 게시물 삭제 시도 - POST_NOT_FOUND 예외 발생")
        void deletePost_postNotFound() {
            // Given: 존재하지 않는 postId
            Long memberId = 1L;
            Long invalidPostId = 999L;

            // When & Then: 예외 발생 확인
            assertThatThrownBy(() -> postDeleteService.deletePost(memberId, invalidPostId))
                    .isInstanceOf(ServiceException.class);
        }

        @Test
        @DisplayName("타인 게시물 삭제 시도 - 소유자 불일치 예외 발생")
        void deletePost_notOwner() {
            // Given: 다른 memberId로 게시물 생성 및 저장
            Long ownerMemberId = 1L;
            Long otherMemberId = 2L;
            Long boardId = 1L;
            Post post = Post.create(boardId, ownerMemberId, "Test Title", "Test Content");
            Post savedPost = postRepository.save(post);
            Long postId = savedPost.getPostId();

            // When & Then: validateMember에서 예외 발생 확인
            assertThatThrownBy(() -> postDeleteService.deletePost(otherMemberId, postId))
                    .isInstanceOf(DomainException.class);
        }
    }
}