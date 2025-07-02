package com.grow.study_service.group.infra.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.group.infra.persistence.entity.GroupJpaEntity;

public interface GroupJpaRepository
	extends JpaRepository<GroupJpaEntity, Long> {
}
