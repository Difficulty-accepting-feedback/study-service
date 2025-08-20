package com.grow.study_service.post.application.find;

import com.grow.study_service.post.presentation.dto.response.PostResponse;
import com.grow.study_service.post.presentation.dto.response.PostSimpleResponse;

import java.util.List;

public interface PostFindService {
    PostResponse findById(Long postId, Long memberId);
    List<PostSimpleResponse> getPostList(Long memberId, Long boardId);
}