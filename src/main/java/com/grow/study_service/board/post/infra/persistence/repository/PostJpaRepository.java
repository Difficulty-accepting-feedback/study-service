package com.grow.study_service.board.post.infra.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.board.post.infra.persistence.entity.PostJpaEntity;

public interface PostJpaRepository
	extends JpaRepository<PostJpaEntity, Long> {
	List<PostJpaEntity> findByBoardId(Long boardId);
}
