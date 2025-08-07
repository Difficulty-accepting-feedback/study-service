package com.grow.study_service.comment.infra.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.comment.infra.persistence.entity.CommentJpaEntity;

public interface CommentJpaRepository
	extends JpaRepository<CommentJpaEntity, Long> {
	List<CommentJpaEntity> findByPostId(Long postId);
}
