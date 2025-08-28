package com.grow.study_service.group.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
	STUDY("스터디"),
	HOBBY("취미"),
	MENTORING("멘토링");

	private final String description;
}
