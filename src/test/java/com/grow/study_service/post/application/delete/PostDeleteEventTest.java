package com.grow.study_service.post.application.delete;

import com.grow.study_service.common.config.RedisConfig;
import com.grow.study_service.post.application.file.event.dto.FileCleanUpEvent;
import com.grow.study_service.post.application.file.event.listener.FileCleanupListener;
import com.grow.study_service.post.application.file.event.schedular.FileCleanupScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
@Import({RedisConfig.class})
@ExtendWith(OutputCaptureExtension.class) // 로그 캡쳐
public class PostDeleteEventTest {

    @Autowired
    private FileCleanupScheduler fileCleanupScheduler;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private FileCleanupListener fileCleanupListener;

    @Value("${file.upload.path}")
    private String uploadPath; // 테스트 용도 경로

    private static final String REDIS_KEY = "failed_file";

    @BeforeEach
    void setup() {
        redisTemplate.delete(REDIS_KEY); // 각 테스트 전 Redis 초기화
    }

    @Nested
    class SuccessCase {
        @Test
        @DisplayName("Redis에 실패 파일이 없고 스케줄러가 정상적으로 스킵되는 경우")
        void testSchedulerSkipsWhenNoFailedFiles(CapturedOutput output) {
            // Given: Redis에 데이터 없음
            Set<String> failedFiles = redisTemplate.opsForSet().members(REDIS_KEY);
            assertThat(failedFiles).isEmpty();

            // When: 스케줄러 실행
            fileCleanupScheduler.cleanupFailedFiles();

            // Then: 로그에 스킵 메시지 확인
            assertThat(output.getOut()).contains("[FILE][CLEANUP][SKIP]");
        }
    }

    @Test
    @DisplayName("이벤트 발행 후 리스너가 파일 삭제에 성공하는 경우")
    void testEventPublishedAndListenerDeletesFileSuccessfully() throws IOException {
        // Given: 테스트용 임시 파일 생성
        Path tempFile = Paths.get(uploadPath, "testfile12.txt");
        Files.createFile(tempFile);

        // When: 이벤트 발행
        FileCleanUpEvent event = new FileCleanUpEvent("testfile12txt", uploadPath + "/testfile12.txt");
        eventPublisher.publishEvent(event);

        // Then: 리스너가 파일 삭제 확인
        fileCleanupListener.handleFileCleanup(event);
        assertThat(Files.notExists(tempFile)).isTrue();
    }

    @Nested
    class FailureCase {

        @Test
        @DisplayName("Redis에 실패 파일 저장 후 스케줄러가 이벤트를 발행하지만 파일 삭제 실패하는 경우")
        void testSchedulerPublishesEventButDeletionFails() throws IOException {
            // Given: Redis에 실패 파일 추가 (존재하지 않는 파일로 설정해 삭제 실패 유발)
            String entry = "nonexistent.txt:/invalid/path";
            redisTemplate.opsForSet().add(REDIS_KEY, entry);

            // When: 스케줄러 실행
            fileCleanupScheduler.cleanupFailedFiles();

            // Then: Redis에서 제거되지 않음 (삭제 실패로 인해), 로그에 실패 메시지
            Set<String> remaining = redisTemplate.opsForSet().members(REDIS_KEY);
            assertThat(remaining).contains(entry);
        }
    }
}
