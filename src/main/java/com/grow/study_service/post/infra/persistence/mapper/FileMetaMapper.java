package com.grow.study_service.post.infra.persistence.mapper;

import com.grow.study_service.post.domain.model.FileMeta;
import com.grow.study_service.post.infra.persistence.entity.FileMetaJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class FileMetaMapper {

    public FileMeta toDomain(FileMetaJpaEntity entity) {
        if (entity == null) return null;
        return FileMeta.of(
                entity.getId(),
                entity.getPostId(),
                entity.getOriginalName(),
                entity.getStoredName(),
                entity.getContentType(),
                entity.getSize(),
                entity.getPath(),
                entity.getUploadedAt()
        );
    }

    public FileMetaJpaEntity toEntity(FileMeta domain) {
        if (domain == null) return null;
        FileMetaJpaEntity.FileMetaJpaEntityBuilder builder = FileMetaJpaEntity.builder()
                .postId(domain.getPostId())
                .originalName(domain.getOriginalName())
                .storedName(domain.getStoredName())
                .contentType(domain.getContentType())
                .size(domain.getSize())
                .path(domain.getPath())
                .uploadedAt(domain.getUploadedAt());

        if (domain.getFileId() != null) {
            builder.id(domain.getFileId());
        }

        return builder.build();
    }
}
