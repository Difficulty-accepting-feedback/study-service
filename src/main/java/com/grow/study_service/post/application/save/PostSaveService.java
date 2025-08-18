package com.grow.study_service.post.application.save;

import com.grow.study_service.post.presentation.dto.request.PostSaveRequest;
import com.grow.study_service.post.presentation.dto.response.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostSaveService {
    PostResponse createPost(Long memberId, PostSaveRequest request, List<MultipartFile> files);
}