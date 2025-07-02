package com.grow.study_service.group.domain.model;

import lombok.Getter;
import java.time.LocalDateTime;

import com.grow.study_service.group.domain.enums.Category;

@Getter
public class Group {
	private Long groupId;
	private String name;
	private Category category;
	private String description;
	private LocalDateTime createdAt;

	private Group(Long groupId, String name, Category category, String description, LocalDateTime createdAt) {
		this.groupId = groupId;
		this.name = name;
		this.category = category;
		this.description = description;
		this.createdAt = createdAt;
	}

	public static Group create(String name, Category category, String description, LocalDateTime now) {
		return new Group(null, name, category, description, now);
	}

	public static Group of(Long groupId, String name, Category category, String description, LocalDateTime createdAt) {
		return new Group(groupId, name, category, description, createdAt);
	}
}
