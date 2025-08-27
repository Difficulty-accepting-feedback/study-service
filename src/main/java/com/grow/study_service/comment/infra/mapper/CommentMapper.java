package com.grow.study_service.comment.infra.mapper;

import com.grow.study_service.comment.domain.model.Comment;
import com.grow.study_service.comment.infra.entity.CommentJpaEntity;
import com.grow.study_service.comment.infra.entity.CommentJpaEntity.CommentJpaEntityBuilder;

public class CommentMapper {
    public static Comment toDomain(CommentJpaEntity e) {
        return Comment.of(
                e.getId(),
                e.getPostId(),
                e.getMemberId(),
                e.getParentId(),
                e.getContent(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.isDeleted(),
                e.getVersion()
        );
    }

    public static CommentJpaEntity toEntity(Comment d) {
        CommentJpaEntityBuilder builder = CommentJpaEntity.builder()
                .postId(d.getPostId())
                .memberId(d.getMemberId())
                .parentId(d.getParentId())
                .content(d.getContent())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .version(d.getVersion() != null ? d.getVersion() : 0L)
                .isDeleted(d.isDeleted());

        if (d.getCommentId() != null) {
            builder.id(d.getCommentId());
        }

        return builder.build();
    }
}