package com.grow.study_service.group.kanbanboard.infra.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.group.kanbanboard.infra.persistence.entity.KanbanBoardJpaEntity;

public interface KanbanBoardJpaRepository
	extends JpaRepository<KanbanBoardJpaEntity, Long> {
	List<KanbanBoardJpaEntity> findByGroupMemberId(Long groupMemberId);
}