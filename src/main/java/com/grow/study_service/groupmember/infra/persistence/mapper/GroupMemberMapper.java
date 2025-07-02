package com.grow.study_service.groupmember.infra.persistence.mapper;

import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.infra.persistence.entity.GroupMemberJpaEntity;

public class GroupMemberMapper {
	public static GroupMember toDomain(GroupMemberJpaEntity e) {
		return GroupMember.of(
				e.getId(),
				e.getMemberId(),
				e.getGroupId(),
				e.getRole(),
				e.getJoinedAt()
		);
	}

	public static GroupMemberJpaEntity toEntity(GroupMember d) {
		return GroupMemberJpaEntity.builder()
			.id(d.getGroupMemberId())
			.memberId(d.getMemberId())
			.groupId(d.getGroupId())
			.role(d.getRole())
			.joinedAt(d.getJoinedAt())
			.build();
	}
}
