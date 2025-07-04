package com.grow.study_service.board.comment.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter

public class Comment {
	private final Long commentId;
	private final Long postId;
	private final Long memberId;
	private Long parentId;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	private Comment(Long commentId, Long postId, Long memberId, Long parentId, String content,
			LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
		this.commentId = commentId;
		this.postId = postId;
		this.memberId = memberId;
		this.parentId = parentId;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
	}

	public static Comment create(Long postId, Long memberId, String content, LocalDateTime now) {
		return new Comment(null, postId, memberId, null, content, now, now, null);
	}

	public void update(String content, LocalDateTime now) {
		if (this.deletedAt != null) {
			throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
		}
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("댓글 내용은 비어 있을 수 없습니다.");
		}
		this.content   = content;
		this.updatedAt = now;
	}

	public void replyTo(Long parentId, LocalDateTime now) {
		this.parentId = parentId;
		this.createdAt = now;
		this.updatedAt = now;
	}

	public void delete(LocalDateTime now) {
		if (this.deletedAt != null) {
			throw new IllegalStateException("이미 삭제된 댓글입니다.");
		}
		this.deletedAt = now;
	}

	public static Comment of(Long commentId, Long postId, Long memberId, Long parentId, String content,
			LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
		return new Comment(commentId, postId, memberId, parentId, content, createdAt, updatedAt, deletedAt);
	}
}
