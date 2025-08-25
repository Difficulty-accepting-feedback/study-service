package com.grow.study_service.comment.infra.mapper;

import com.grow.study_service.comment.domain.model.Comment;
import com.grow.study_service.comment.infra.entity.CommentJpaEntity;

public class CommentMapper {
	public static Comment toDomain(CommentJpaEntity e) {
		return Comment.of(
			e.getId(),
			e.getPostId(),
			e.getMemberId(),
			e.getParentId(),
			e.getContent(),
			e.getCreatedAt(),
			e.getUpdatedAt()
		);
	}
	public static CommentJpaEntity toEntity(Comment d) {
		return CommentJpaEntity.builder()
			.id(d.getCommentId())
			.postId(d.getPostId())
			.memberId(d.getMemberId())
			.parentId(d.getParentId())
			.content(d.getContent())
			.createdAt(d.getCreatedAt())
			.updatedAt(d.getUpdatedAt())
			.build();
	}
}