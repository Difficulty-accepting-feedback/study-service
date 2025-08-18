package com.grow.study_service.post.infra.persistence.mapper;

import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.infra.persistence.entity.PostJpaEntity;

import static com.grow.study_service.post.infra.persistence.entity.PostJpaEntity.*;

public class PostMapper {

    /**
     * PostJpaEntity를 Post 도메인으로 변환 (조회)
     */
    public static Post toDomain(PostJpaEntity e) {
        return Post.of(
                e.getId(),
                e.getBoardId(),
                e.getMemberId(),
                e.getTitle(),
                e.getContent(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    /**
     * Post 도메인을 PostJpaEntity로 변환 (저장 및 수정)
     */
    public static PostJpaEntity toEntity(Post d) {
        PostJpaEntityBuilder builder = builder()
                .boardId(d.getBoardId())
                .memberId(d.getMemberId())
                .title(d.getTitle())
                .content(d.getContent())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt());

        if (d.getPostId() != null) {
            builder.id(d.getPostId());
        }

        return builder.build();
    }
}
