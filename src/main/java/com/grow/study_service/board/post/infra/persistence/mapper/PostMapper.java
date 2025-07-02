package com.grow.study_service.board.post.infra.persistence.mapper;

import com.grow.study_service.board.post.domain.model.Post;
import com.grow.study_service.board.post.infra.persistence.entity.PostJpaEntity;

public class PostMapper {
	public static Post toDomain(PostJpaEntity e) {
		return Post.of(
			e.getId(),
			e.getBoardId(),
			e.getMemberId(),
			e.getTitle(),
			e.getContent(),
			e.getFileUrl(),
			e.getCreatedAt(),
			e.getUpdatedAt()
		);
	}

	public static PostJpaEntity toEntity(Post d) {
		return PostJpaEntity.builder()
			.id(d.getPostId())
			.boardId(d.getBoardId())
			.memberId(d.getMemberId())
			.title(d.getTitle())
			.content(d.getContent())
			.fileUrl(d.getFileUrl())
			.createdAt(d.getCreatedAt())
			.updatedAt(d.getUpdatedAt())
			.build();
	}
}
