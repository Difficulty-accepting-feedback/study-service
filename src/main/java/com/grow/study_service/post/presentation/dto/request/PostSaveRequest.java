package com.grow.study_service.post.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 저장 요청 DTO
 * 클라이언트 -> 서버 전송 시 사용
 */
@Getter
@AllArgsConstructor
public class PostSaveRequest {

    @NotNull(message = "게시판 ID는 필수입니다.")
    private Long boardId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 가능합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}