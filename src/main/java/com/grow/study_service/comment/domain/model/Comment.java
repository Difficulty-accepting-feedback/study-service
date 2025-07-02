package com.grow.study_service.comment.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter

public class Comment {
	private Long commentId;
	private Long postId;
	private Long memberId;
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
		this.content = content;
		this.updatedAt = now;
	}

	public void replyTo(Long parentId, LocalDateTime now) {
		this.parentId = parentId;
		this.createdAt = now;
		this.updatedAt = now;
	}

	public static Comment of(Long commentId, Long postId, Long memberId, Long parentId, String content,
			LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
		return new Comment(commentId, postId, memberId, parentId, content, createdAt, updatedAt, deletedAt);
	}
}
