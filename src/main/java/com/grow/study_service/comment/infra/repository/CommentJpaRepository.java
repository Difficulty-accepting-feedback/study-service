package com.grow.study_service.comment.infra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.comment.infra.entity.CommentJpaEntity;

public interface CommentJpaRepository
	extends JpaRepository<CommentJpaEntity, Long> {
	List<CommentJpaEntity> findByPostId(Long postId);
	boolean existsByPostIdAndMemberIdAndContent(Long postId, Long memberId, String content);

	// 해당 게시글의 모든 댓글을 parentId 오름차순, createdAt 오름차순으로 조회
	List<CommentJpaEntity> findByPostIdOrderByParentIdAscCreatedAtAsc(Long postId);
}
