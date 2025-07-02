package com.grow.study_service.board.post.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class Post {
	private final Long postId;
	private final Long boardId;
	private final Long memberId;
	private final LocalDateTime createdAt;
	private String title;
	private String content;
	private String fileUrl;
	private LocalDateTime updatedAt;

	private Post(Long postId, Long boardId, Long memberId, String title, String content, String fileUrl,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.postId = postId;
		this.boardId = boardId;
		this.memberId = memberId;
		this.title = title;
		this.content = content;
		this.fileUrl = fileUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static Post create(Long boardId, Long memberId, String title, String content, String fileUrl, LocalDateTime now) {
		return new Post(null, boardId, memberId, title, content, fileUrl, now, now);
	}

	public void update(String title, String content, String fileUrl, LocalDateTime now) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("제목은 비어 있을 수 없습니다.");
		}
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("내용은 비어 있을 수 없습니다.");
		}
		this.title = title;
		this.content = content;
		this.fileUrl = fileUrl;
		this.updatedAt = now;
	}

	public static Post of(Long postId, Long boardId, Long memberId, String title, String content, String fileUrl,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		return new Post(postId, boardId, memberId, title, content, fileUrl, createdAt, updatedAt);
	}
}
