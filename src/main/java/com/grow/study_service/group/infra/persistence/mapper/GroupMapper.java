package com.grow.study_service.group.infra.persistence.mapper;

import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.group.infra.persistence.entity.GroupJpaEntity;

public class GroupMapper {
	public static Group toDomain(GroupJpaEntity e) {
		return Group.of(
				e.getId(),
				e.getName(),
				e.getCategory(),
				e.getDescription(),
				e.getCreatedAt()
		);
	}

	public static GroupJpaEntity toEntity(Group d) {
		return GroupJpaEntity.builder()
				.id(d.getGroupId())
				.name(d.getName())
				.category(d.getCategory())
				.description(d.getDescription())
				.createdAt(d.getCreatedAt())
				.build();
	}
}
