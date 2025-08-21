package com.grow.study_service.post.infra.persistence.repository.file;

import com.grow.study_service.post.infra.persistence.entity.FileMetaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetaJpaRepository extends JpaRepository<FileMetaJpaEntity, Long> {
    List<FileMetaJpaEntity> findAllByPostId(Long postId);
    List<FileMetaJpaEntity> findByPostId(Long postId);
}
