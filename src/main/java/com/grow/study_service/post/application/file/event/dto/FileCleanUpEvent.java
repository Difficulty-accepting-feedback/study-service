package com.grow.study_service.post.application.file.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileCleanUpEvent {

    private final String storedName;
    private final String path;
}
