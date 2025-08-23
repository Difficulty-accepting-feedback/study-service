package com.grow.study_service.post.application.file.event.listener;

import com.grow.study_service.post.application.file.event.dto.FileCleanUpEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileCleanupListener {

    @EventListener
    public void handleFileCleanup(FileCleanUpEvent event) {
        String storedName = event.getStoredName();
        String storedPath = event.getPath();
        Path filePath = Paths.get(storedPath);

        try {
            Files.deleteIfExists(filePath);
            log.info("[FILE CLEANUP EVENT][SUCCESS] 파일 삭제 완료 storedName={}, storedPath={}", storedName, storedPath);
        } catch (IOException e) {
            log.error("[FILE CLEANUP EVENT][FAILED] 파일 삭제 실패 storedName={}, storedPath={}", storedName, storedPath, e);
        }
    }
}
