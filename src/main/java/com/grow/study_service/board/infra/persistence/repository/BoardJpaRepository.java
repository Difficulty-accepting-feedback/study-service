package com.grow.study_service.board.infra.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.board.infra.persistence.entity.BoardJpaEntity;

public interface BoardJpaRepository
	extends JpaRepository<BoardJpaEntity, Long> {
	List<BoardJpaEntity> findByGroupId(Long groupId);
}
