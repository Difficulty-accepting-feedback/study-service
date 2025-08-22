package com.grow.study_service.post.application.file.save;

import com.grow.study_service.post.domain.model.FileMeta;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<FileMeta> storeFilesForPost(Long postId, List<MultipartFile> files);
    Resource getDownloadLink(Long fileId);
    FileMeta getFileMeta(Long fileId);
    void deleteFilesForPost(Long postId);
}
