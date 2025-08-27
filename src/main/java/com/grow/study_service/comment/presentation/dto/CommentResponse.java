package com.grow.study_service.comment.presentation.dto;

import com.grow.study_service.comment.domain.model.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// TODO: 추후 페이징 처리, Member 에서 닉네임 가져오기
@Getter
@Builder
public class CommentResponse {

    private Long commentId;
    private Long parentId;
    private Long memberId; // 작성자 ID
    private String content;
    private LocalDateTime createdAt; // 작성 시간
    private LocalDateTime updatedAt; // 수정 시간 (댓글 수정 시 사용), null 가능
    private List<CommentResponse> replies;  // 대댓글 리스트

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .parentId(comment.getParentId())
                .memberId(comment.getMemberId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(List.of())  // 기본 빈 리스트 (재귀에서 채움)
                .build();
    }
}
