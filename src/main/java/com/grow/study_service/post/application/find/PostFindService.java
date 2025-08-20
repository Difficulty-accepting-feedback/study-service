package com.grow.study_service.post.application.find;

import com.grow.study_service.post.presentation.dto.response.PostResponse;

public interface PostFindService {
    PostResponse findById(Long postId, Long memberId);
}