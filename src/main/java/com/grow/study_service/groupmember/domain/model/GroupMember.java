package com.grow.study_service.groupmember.domain.model;

import java.time.LocalDateTime;

import com.grow.study_service.groupmember.domain.enums.Role;

import lombok.Getter;

@Getter
public class GroupMember {
	private Long groupMemberId;
	private Long memberId;
	private Long groupId;
	private Role role;
	private LocalDateTime joinedAt;

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
