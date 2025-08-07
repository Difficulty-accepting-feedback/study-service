package com.grow.study_service.board.infra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.board.infra.entity.BoardJpaEntity;

public interface BoardJpaRepository
	extends JpaRepository<BoardJpaEntity, Long> {
	List<BoardJpaEntity> findByGroupId(Long groupId);
}
