package com.grow.study_service.post.application.file;

import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.post.application.file.delete.FileDeleteService;
import com.grow.study_service.post.application.file.save.FileService;
import com.grow.study_service.post.domain.model.FileMeta;
import com.grow.study_service.post.domain.repository.FileMetaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class FileServiceImplTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileDeleteService fileDeleteService;

    @MockitoBean
    private FileMetaRepository fileMetaRepository;

    // uploadPath는 테스트 프로필에서 설정된 값을 사용

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("파일 저장 성공 - 메타 저장 및 반환")
        void storeFilesForPost_success() throws IOException {
            // Given: 테스트 파일 생성
            Long postId = 1L;
            MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
            List<MultipartFile> files = List.of(mockFile);

            // FileMetaRepository 모킹: 저장 시 반환 (팩토리 메서드 사용)
            String storedName = UUID.randomUUID().toString() + ".txt";
            String path = "target/test-uploads/" + storedName;
            LocalDateTime now = LocalDateTime.now();
            FileMeta mockMeta = FileMeta.of(1L, postId, "test.txt", storedName, "text/plain", mockFile.getSize(), path, now);
            when(fileMetaRepository.save(any(FileMeta.class))).thenReturn(mockMeta);

            // When: 파일 저장 메서드 호출
            List<FileMeta> result = fileService.storeFilesForPost(postId, files);

            // Then: 결과 확인
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOriginalName()).isEqualTo("test.txt");

            // 메타 저장 호출 확인
            verify(fileMetaRepository).save(any(FileMeta.class));
        }

        @Test
        @DisplayName("파일 삭제 성공 - 메타 및 물리 파일 삭제")
        void deleteFilesForPost_success() throws IOException {
            // Given: 테스트 메타 및 파일 설정
            Long postId = 1L;
            String storedName = "a.txt";
            String path = "build/tmp/test-uploads/" + storedName;
            LocalDateTime now = LocalDateTime.now();
            FileMeta meta = FileMeta.of(1L, postId, "test.txt", storedName, "text/plain", 7L, path, now);
            when(fileMetaRepository.findAllByPostId(postId)).thenReturn(List.of(meta));

            // 실제 테스트 파일 생성
            fileService.storeFilesForPost(postId, List.of(new MockMultipartFile("file", "a.txt", "text/plain", "content".getBytes())));

            // When: 삭제 메서드 호출
            fileDeleteService.deleteFilesForPost(postId);

            // 파일 삭제 확인
            assertThat(Files.exists(Paths.get(path))).isFalse();
        }

        @Test
        @DisplayName("다운로드 링크 가져오기 성공")
        void getDownloadLink_success() {
            // Given: 테스트 메타 설정
            Long fileId = 1L;
            String storedName = "b.txt";
            String path = "build/tmp/test-uploads/" + storedName;
            LocalDateTime now = LocalDateTime.now();
            FileMeta meta = FileMeta.of(fileId, 10L, "test.txt", storedName, "text/plain", 7L, path, now);
            when(fileMetaRepository.findById(fileId)).thenReturn(Optional.of(meta));

            // 실제 테스트 파일 생성
            fileService.storeFilesForPost(10L, List.of(new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes())));

            // When: 링크 가져오기
            Resource resource = fileService.getDownloadLink(fileId);

            // Then: 리소스 확인
            assertThat(resource).isNotNull();
        }

        @Test
        @DisplayName("파일 메타 가져오기 성공")
        void getFileMeta_success() {
            // Given: 테스트 메타 설정 (팩토리 메서드 사용)
            Long fileId = 1L;
            String storedName = UUID.randomUUID().toString() + ".txt";
            String path = "target/test-uploads/" + storedName;
            LocalDateTime now = LocalDateTime.now();
            FileMeta meta = FileMeta.of(fileId, 1L, "test.txt", storedName, "text/plain", 7L, path, now);
            when(fileMetaRepository.findById(fileId)).thenReturn(Optional.of(meta));

            // When: 메타 가져오기
            FileMeta result = fileService.getFileMeta(fileId);

            // Then: 결과 확인
            assertThat(result).isEqualTo(meta);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("존재하지 않는 파일 다운로드 시도 - 예외 발생")
        void getDownloadLink_fileNotFound() {
            // Given: 존재하지 않는 fileId
            Long invalidFileId = 999L;
            when(fileMetaRepository.findById(invalidFileId)).thenReturn(Optional.empty());

            // When & Then: 예외 발생 확인
            assertThatThrownBy(() -> fileService.getDownloadLink(invalidFileId))
                    .isInstanceOf(ServiceException.class);
        }

        @Test
        @DisplayName("존재하지 않는 파일 메타 가져오기 - 예외 발생")
        void getFileMeta_fileNotFound() {
            // Given: 존재하지 않는 fileId
            Long invalidFileId = 999L;
            when(fileMetaRepository.findById(invalidFileId)).thenReturn(Optional.empty());

            // When & Then: 예외 발생 확인
            assertThatThrownBy(() -> fileService.getFileMeta(invalidFileId))
                    .isInstanceOf(ServiceException.class);
        }
    }
}