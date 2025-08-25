package com.grow.study_service.comment.domain.model;

import java.time.LocalDateTime;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Comment {
    private final Long commentId;
    private final Long postId;
    private final Long memberId;
    private Long parentId; // 댓글의 부모 댓글 ID
    private String content; // 댓글 내용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성 메서드
    public static Comment create(Long postId, Long memberId,
                                 Long parentId, String content, LocalDateTime now) {
        return new Comment(
                null,
                postId,
                memberId,
                parentId,
                content,
                now,
                now
        );
    }

    // 조회 메서드
    public static Comment of(Long commentId, Long postId, Long memberId,
                             Long parentId, String content,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Comment(
                commentId,
                postId,
                memberId,
                parentId,
                content,
                createdAt,
                updatedAt
        );
    }

    public void update(String content) {
        if (content == null || content.isBlank()) {
            throw new DomainException(ErrorCode.COMMENT_CONTENT_IS_EMPTY);
        }

        if (!this.content.equals(content)) { // 내용이 변경되었으면
            this.content = content;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void replyTo(Long parentId) { 
        this.parentId = parentId;
        this.createdAt = LocalDateTime.now();
    }
}
