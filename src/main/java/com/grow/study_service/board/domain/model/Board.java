package com.grow.study_service.board.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import com.grow.study_service.post.domain.model.Post;

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
		if (newName == null || newName.isBlank()) {
			throw new IllegalArgumentException("게시판 이름은 비어 있을 수 없습니다.");
		}
		if (this.name.equals(newName)) {
			throw new IllegalStateException("변경할 게시판 이름이 현재 이름과 동일합니다.");
		}
		this.name = newName;
	}

	public void changeDescription(String newDescription) {
		if (newDescription == null) {
			throw new IllegalArgumentException("게시판 설명은 비어 있을 수 없습니다.");
		}
		this.description = newDescription;
	}

	public static Board of(Long boardId, Long groupId, String name, String description, LocalDateTime createdAt, List<Post> posts) {
		return new Board(boardId, groupId, name, description, createdAt, posts);
	}
}
