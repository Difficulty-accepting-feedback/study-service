package com.grow.study_service.post.domain.repository;

import com.grow.study_service.post.domain.model.FileMeta;

import java.util.List;

public interface FileMetaRepository {
    FileMeta save(FileMeta fileMeta);
    List<FileMeta> findAllByPostId(Long postId);
}
