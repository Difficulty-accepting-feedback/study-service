package com.grow.study_service.group.domain.model;

import lombok.Getter;
import java.time.LocalDateTime;

import com.grow.study_service.group.domain.enums.Category;

@Getter
public class Group {
	private final Long groupId;
	private final Category category;
	private final LocalDateTime createdAt;
	private String name;
	private String description;


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

	public void rename(String newName) {
		if (newName == null || newName.isBlank()) {
			throw new IllegalArgumentException("그룹 이름은 비어 있을 수 없습니다.");
		}
		if (this.name.equals(newName)) {
			throw new IllegalStateException("변경할 그룹 이름이 현재 이름과 동일합니다.");
		}
		this.name = newName;
	}

	public void updateDescription(String newDescription) {
		if (newDescription == null || newDescription.isBlank()) {
			throw new IllegalArgumentException("그룹 설명은 비어 있을 수 없습니다.");
		}
		this.description = newDescription;
	}

	public static Group of(Long groupId, String name, Category category, String description, LocalDateTime createdAt) {
		return new Group(groupId, name, category, description, createdAt);
	}
}
