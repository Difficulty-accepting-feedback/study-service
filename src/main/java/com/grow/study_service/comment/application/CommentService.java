package com.grow.study_service.comment.application;

import com.grow.study_service.comment.presentation.dto.CommentResponse;
import com.grow.study_service.comment.presentation.dto.CommentSaveRequest;

import java.util.List;

public interface CommentService {
    CommentResponse save(Long memberId, Long postId, CommentSaveRequest request);
    List<CommentResponse> getCommentsByPostId(Long postId, Long memberId);
    CommentResponse updateComment(Long memberId, Long postId, Long commentId, CommentSaveRequest request);
    void deleteComment(Long commentId, Long postId, Long memberId);
}