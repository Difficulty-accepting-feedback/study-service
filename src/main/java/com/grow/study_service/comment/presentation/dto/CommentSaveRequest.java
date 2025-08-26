package com.grow.study_service.comment.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentSaveRequest {

    private String content;
    private Long parentId; // 부모 댓글 아이디, 없어도 됨 (null 일 경우 첫 댓글)
}
