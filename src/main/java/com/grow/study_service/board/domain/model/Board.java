package com.grow.study_service.board.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import com.grow.study_service.board.post.domain.model.Post;

import lombok.Getter;

@Getter
public class Board {
	private final Long boardId;
	private final Long groupId;
	private final List<Post> posts;
	private final LocalDateTime createdAt;
	private String name;
	private String description;

	private Board(Long boardId, Long groupId, String name, String description, LocalDateTime createdAt, List<Post> posts) {
		this.boardId = boardId;
		this.groupId = groupId;
		this.name = name;
		this.description = description;
		this.createdAt = createdAt;
		this.posts = posts;
	}

	public static Board create(Long groupId, String name, String description, LocalDateTime now) {
		return new Board(null, groupId, name, description, now, null);
	}

	public void rename(String newName) {
		this.name = newName;
	}

	public void changeDescription(String newDesc) {
		this.description = newDesc;
	}

	public static Board of(Long boardId, Long groupId, String name, String description, LocalDateTime createdAt, List<Post> posts) {
		return new Board(boardId, groupId, name, description, createdAt, posts);
	}
}
