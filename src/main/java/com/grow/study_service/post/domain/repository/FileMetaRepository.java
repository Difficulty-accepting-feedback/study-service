package com.grow.study_service.post.domain.repository;

import com.grow.study_service.post.domain.model.FileMeta;

import java.util.List;
import java.util.Optional;

public interface FileMetaRepository {
    FileMeta save(FileMeta fileMeta);
    List<FileMeta> findAllByPostId(Long postId);
    Optional<FileMeta> findById(Long fileId);
    List<FileMeta> findByPostId(Long postId);
    void delete(FileMeta meta);
}
