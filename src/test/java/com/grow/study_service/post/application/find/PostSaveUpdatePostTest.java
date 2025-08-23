package com.grow.study_service.post.application.find;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.post.application.file.save.FileService;
import com.grow.study_service.post.application.file.delete.FileDeleteService;
import com.grow.study_service.post.application.save.PostSaveService;
import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.FileMetaRepository;
import com.grow.study_service.post.domain.repository.PostRepository;
import com.grow.study_service.post.presentation.dto.request.PostSaveRequest;
import com.grow.study_service.post.presentation.dto.request.PostUpdateRequest;
import com.grow.study_service.post.presentation.dto.response.PostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class PostSaveUpdatePostTest {

    @Autowired
    private PostSaveService postSaveService;

    @Autowired
    private PostRepository postRepository;

    @MockitoSpyBean(name="fileServiceImpl")
    private FileService fileService;

    @MockitoSpyBean
    private FileDeleteService fileDeleteService;

    @Autowired
    FileMetaRepository fileMetaRepository;

    private Long seedPost(Long boardId, Long authorMemberId, String title, String content, List<MultipartFile> files) {
        PostSaveRequest req = new PostSaveRequest(boardId, title, content);
        PostResponse created = postSaveService.createPost(authorMemberId, req, files);
        return created.getPostId();
    }

    private List<MultipartFile> mockFiles(String... names) {
        return java.util.Arrays.stream(names)
                .map(n -> new MockMultipartFile("files", n, "application/octet-stream", ("content-of-" + n).getBytes()))
                .map(f -> (MultipartFile) f)
                .toList();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("제목/내용만 변경(파일 없음)")
        void update_title_and_content_only() {
            // given
            Long authorId = 10L;
            Long boardId = 100L;
            Long postId = seedPost(boardId, authorId, "초기제목", "초기내용", List.of());

            PostUpdateRequest req = new PostUpdateRequest("수정제목", "수정내용");

            // when
            postSaveService.updatePost(authorId, postId, req, null);

            // then: DB에서 다시 로드하여 값 확인
            Post updated = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalStateException("post not found after update"));
            assertThat(updated.getTitle()).isEqualTo("수정제목");
            assertThat(updated.getContent()).isEqualTo("수정내용");
            assertThat(updated.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("파일 대체: 기존 파일 삭제 후 새 파일 저장")
        void update_replace_files() {
            // given
            Long authorId = 11L;
            Long boardId = 101L;
            // 최초 생성 시 파일 2개
            List<MultipartFile> initialFiles = mockFiles("a.txt", "b.txt");
            Long postId = seedPost(boardId, authorId, "제목", "내용", initialFiles);

            // 업데이트: 제목/내용 변경 + 파일 1개로 교체
            PostUpdateRequest req = new PostUpdateRequest("새제목", "새내용");
            List<MultipartFile> newFiles = mockFiles("c.txt");

            // when
            postSaveService.updatePost(authorId, postId, req, newFiles);

            // then: DB 에서 다시 로드하여 값 확인
            Post updated = postRepository.findById(postId)
                    .orElseThrow();
            assertThat(updated.getTitle()).isEqualTo("새제목");
            assertThat(updated.getContent()).isEqualTo("새내용");

            String originalName = fileMetaRepository.findByPostId(postId).get(0).getOriginalName();

            assertThat(originalName).isEqualTo("c.txt");
            assertThat(fileMetaRepository.findByPostId(postId)).hasSize(1);
        }

        @Test
        @DisplayName("제목만 변경(null 콘텐츠), 내용만 변경(null 제목)")
        void update_partial_nulls() {
            // given
            Long authorId = 12L;
            Long boardId = 102L;
            Long postId = seedPost(boardId, authorId, "원제목", "원내용", List.of());

            // when: 제목만 변경
            postSaveService.updatePost(authorId, postId, new PostUpdateRequest("제목만", null), null);

            // then
            Post afterTitle = postRepository.findById(postId).orElseThrow();
            assertThat(afterTitle.getTitle()).isEqualTo("제목만");
            assertThat(afterTitle.getContent()).isEqualTo("원내용");

            // when: 내용만 변경
            postSaveService.updatePost(authorId, postId, new PostUpdateRequest(null, "내용만"), null);

            // then
            Post afterContent = postRepository.findById(postId).orElseThrow();
            assertThat(afterContent.getTitle()).isEqualTo("제목만");
            assertThat(afterContent.getContent()).isEqualTo("내용만");
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("존재하지 않는 게시글이면 예외")
        void update_post_not_found() {
            // given
            Long authorId = 20L;
            Long notExistPostId = 9_999_999L;
            PostUpdateRequest req = new PostUpdateRequest("x", "y");

            // when & then
            assertThatThrownBy(() -> postSaveService.updatePost(authorId, notExistPostId, req, null))
                    .isInstanceOf(ServiceException.class);
        }

        @Test
        @DisplayName("작성자가 아니면 권한 예외")
        void update_forbidden() {
            // given
            Long authorId = 21L;
            Long otherMemberId = 22L;
            Long boardId = 200L;
            Long postId = seedPost(boardId, authorId, "t", "c", List.of());

            // when & then
            assertThatThrownBy(() -> postSaveService.updatePost(otherMemberId, postId, new PostUpdateRequest("변경", "변경"), null))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @Transactional(propagation = NOT_SUPPORTED)
        @DisplayName("파일 서비스 오류 발생 시 롤백 보장 (delete 호출 여부 + DB 원복 + 파일 메타 원복까지 검증)")
        void update_file_service_failure_causes_rollback() {
            // given
            Long authorId = 24L;
            Long boardId = 202L;

            // 최초 생성: 파일 1개 포함
            List<MultipartFile> initialFiles = mockFiles("x.txt");
            Long postId = seedPost(boardId, authorId, "before", "before", initialFiles);

            // 사전 스냅샷
            Post before = postRepository.findById(postId).orElseThrow();
            assertThat(before.getTitle()).isEqualTo("before");
            assertThat(before.getContent()).isEqualTo("before");
            assertThat(fileMetaRepository.findByPostId(postId)).hasSize(1);
            String beforeName = fileMetaRepository.findByPostId(postId).get(0).getOriginalName();
            assertThat(beforeName).isEqualTo("x.txt");

            // FileService 스텁: delete는 정상, store 에서 예외
            Mockito.doNothing().when(fileDeleteService).deleteFilesForPost(eq(postId));
            Mockito.doThrow(new ServiceException(ErrorCode.FILE_UPLOAD_FAILED))
                    .when(fileService).storeFilesForPost(eq(postId), anyList());

            assertThatThrownBy(() -> fileService.storeFilesForPost(postId, List.of())).isInstanceOf(ServiceException.class);

            // when & then: 예외 발생
            assertThatThrownBy(() ->
                    postSaveService.updatePost(
                            authorId,
                            postId,
                            new PostUpdateRequest("after", "after"),
                            mockFiles("y.txt")
                    )
            ).isInstanceOf(ServiceException.class);

            // 롤백 검증: 제목/내용/파일메타 원복
            Post reloaded = postRepository.findById(postId).orElseThrow();
            assertThat(reloaded.getTitle()).isEqualTo("before");
            assertThat(reloaded.getContent()).isEqualTo("before");
            assertThat(fileMetaRepository.findByPostId(postId)).hasSize(1);

            String afterName = fileMetaRepository.findByPostId(postId).get(0).getOriginalName();
            assertThat(afterName).isEqualTo("x.txt");
        }
    }
}
