package com.grow.study_service.post.presentation.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostUpdateRequest {

    @Size(max = 100, message = "제목은 최대 100자까지 가능합니다.")
    private String title; // null 가능
    private String content; // null 가능
}
