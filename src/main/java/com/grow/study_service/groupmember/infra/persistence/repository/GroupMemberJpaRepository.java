package com.grow.study_service.groupmember.infra.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.groupmember.infra.persistence.entity.GroupMemberJpaEntity;

public interface GroupMemberJpaRepository
	extends JpaRepository<GroupMemberJpaEntity, Long> {
	Optional<GroupMemberJpaEntity> findByMemberIdAndGroupId(Long memberId, Long groupId);
	boolean existsByGroupIdAndMemberId(Long groupId, Long memberId);
}
