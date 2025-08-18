package com.grow.study_service.post.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileMetaResponse {
    private Long fileId;
    private String originalName;
    private String storedName;
    private String contentType;
    private long size;
    private String path;
}