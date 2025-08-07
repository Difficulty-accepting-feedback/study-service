package com.grow.study_service.groupmember.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
	LEADER("리더"), // 그룹 리더 (운영자)
	MEMBER("멤버"); // 그룹 멤버

	private final String info;
}
