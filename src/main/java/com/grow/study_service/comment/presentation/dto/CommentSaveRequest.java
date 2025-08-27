package com.grow.study_service.comment.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentSaveRequest {

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(min = 1, max = 500, message = "댓글 내용은 1자 이상 500자 이하로 입력해주세요.")
    private String content;

    @Positive(message = "부모 댓글 ID는 양수여야 합니다.") // null 허용, 하지만 값이 있으면 양수만 허용
    private Long parentId; // 부모 댓글 아이디, 없어도 됨 (null 일 경우 첫 댓글)
}
