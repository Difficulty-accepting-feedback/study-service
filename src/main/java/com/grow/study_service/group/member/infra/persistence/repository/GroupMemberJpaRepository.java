package com.grow.study_service.group.member.infra.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.group.member.infra.persistence.entity.GroupMemberJpaEntity;

public interface GroupMemberJpaRepository
	extends JpaRepository<GroupMemberJpaEntity, Long> {
	List<GroupMemberJpaEntity> findByGroupId(Long groupId);
}
