package com.grow.study_service.post.presentation.controller;

import com.grow.study_service.post.application.file.FileService;
import com.grow.study_service.post.domain.model.FileMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class FileController {

    private final FileService fileService;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @RequestHeader("X-Authorization-Id") Long memberId,
            @PathVariable("fileId") Long fileId) {

        // FileService를 통해 Resource와 FileMeta 가져오기
        Resource resource = fileService.getDownloadLink(fileId);
        FileMeta fileMeta = fileService.getFileMeta(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileMeta.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileMeta.getOriginalName() + "\"")
                .body(resource);
    }
}
