package com.grow.study_service.post.application.file;

import com.grow.study_service.post.domain.model.FileMeta;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<FileMeta> storeFilesForPost(Long postId, List<MultipartFile> files);
}
