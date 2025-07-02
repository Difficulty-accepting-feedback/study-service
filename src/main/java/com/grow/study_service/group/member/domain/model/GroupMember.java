package com.grow.study_service.group.member.domain.model;

import java.time.LocalDateTime;

import com.grow.study_service.group.member.domain.enums.Role;

import lombok.Getter;

@Getter
public class GroupMember {
	private final Long groupMemberId;
	private final Long memberId;
	private final Long groupId;
	private final Role role;
	private final LocalDateTime joinedAt;

	private GroupMember(Long groupMemberId, Long memberId, Long groupId, Role role, LocalDateTime joinedAt) {
		this.groupMemberId = groupMemberId;
		this.memberId = memberId;
		this.groupId = groupId;
		this.role = role;
		this.joinedAt = joinedAt;
	}

	public static GroupMember create(Long memberId, Long groupId, Role role, LocalDateTime now) {
		return new GroupMember(null, memberId, groupId, role, now);
	}

	public static GroupMember of(Long groupMemberId, Long memberId, Long groupId, Role role, LocalDateTime joinedAt) {
		return new GroupMember(groupMemberId, memberId, groupId, role, joinedAt);
	}
}
