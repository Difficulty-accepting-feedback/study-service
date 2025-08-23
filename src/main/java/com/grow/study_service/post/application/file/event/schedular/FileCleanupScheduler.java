package com.grow.study_service.post.application.file.event.schedular;

import com.grow.study_service.post.application.file.event.dto.FileCleanUpEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileCleanupScheduler {

    @Value("${file.upload.path}")
    private String uploadPath;

    private final RedisTemplate<String, String> redisTemplate;
    private final ApplicationEventPublisher publisher;
    private static final String REDIS_KEY = "failed_file";

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    public void cleanupFailedFiles() {
        // redis 에서 실패한 파일의 목록 가져오기 (storedName:path)
        Set<String> failedFiles = redisTemplate.opsForSet().members(REDIS_KEY);

        if (failedFiles == null || failedFiles.isEmpty()) {
            log.info("[FILE][CLEANUP][SKIP] failedFiles가 비어있습니다.");
            return;
        }

        // 파일 스캔, 디렉토리의 Path 객체를 생성 및 탐색 (자동 리소스 해제)
        try (Stream<Path> paths = Files.walk(Paths.get(uploadPath))) {
            paths.filter(Files::isRegularFile) // 파일인지 확인
                    .forEach(path -> {
                        String fileName = path.getFileName().toString(); // 현재 파일의 파일명을 문자열로 추출
                        failedFiles.stream()
                                .filter(entry -> entry.startsWith(fileName + ":")) // 파일명이 일치하는 경우
                                .findFirst()
                                .ifPresent(entry -> {
                                    String[] parts = entry.split(":");
                                    String storedName = parts[0];
                                    String storedPath = parts[1];

                                    // 파일이 아직 존재하면 이벤트 발행
                                    if (Files.exists(path)) {
                                        publisher.publishEvent(new FileCleanUpEvent(storedName, storedPath));

                                        // redis 에서 제거
                                        redisTemplate.opsForSet().remove(REDIS_KEY, entry);
                                        log.info("[FILE][SCHEDULER][SUCCESS] 파일 스캔 완료, 파일 삭제 완료: storedName={}, storedPath={}",
                                                storedName, storedPath);
                                    }
                                });
                    });

        } catch (IOException ignored) {
            log.warn("[FILE][CLEANUP][FAILED] 파일 스캔 중 예외 발생: {}", ignored.toString());
            // 이후에 다시 실행할 예정이니 그냥 무시
        }
    }
}
