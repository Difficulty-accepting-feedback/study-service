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
    private boolean isDeleted;
    private Long version;

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
                now,
                false,
                null
        );
    }

    // 조회 메서드
    public static Comment of(Long commentId, Long postId,
                             Long memberId, Long parentId,
                             String content, LocalDateTime createdAt,
                             LocalDateTime updatedAt, boolean isDeleted,
                             Long version) {
        return new Comment(
                commentId,
                postId,
                memberId,
                parentId,
                content,
                createdAt,
                updatedAt,
                isDeleted,
                version
        );
    }

    public Comment update(String content) {
        if (content == null || content.isBlank()) {
            throw new DomainException(ErrorCode.COMMENT_CONTENT_IS_EMPTY);
        }

        if (!this.content.equals(content)) { // 내용이 변경되었으면
            this.content = content;
            this.updatedAt = LocalDateTime.now();
        }

        return this; // 변경된 객체를 반환
    }

    public void validateMemberId(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new DomainException(ErrorCode.INVALID_COMMENT_ACCESS);
        }
    }

    public Comment softDelete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
        this.content = "삭제된 댓글입니다."; // 댓글 내용을 변경하여 삭제된 댓글을 표시할 수 있도록 함

        return this; // 변경된 객체를 반환
    }

    public void changeContent(String str) {
        this.content = str;
    }
}
