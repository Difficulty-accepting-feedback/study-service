package com.grow.study_service.comment.presentation.controller;

import com.grow.study_service.comment.application.CommentService;
import com.grow.study_service.comment.presentation.dto.CommentResponse;
import com.grow.study_service.comment.presentation.dto.CommentSaveRequest;
import com.grow.study_service.common.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 저장 API
    @PostMapping("{postId}")
    public RsData<CommentResponse> saveComment(@RequestHeader("X-Authorization-Id") Long memberId,
                                               @PathVariable("postId") Long postId,
                                               @RequestBody CommentSaveRequest request) {

        CommentResponse response = commentService.save(memberId, postId, request);

        return new RsData<>(
                "201",
                "댓글 저장 완료",
                response
        );
    }

    // 댓글 조회 API
    @GetMapping("{postId}")
    public RsData<List<CommentResponse>> getComments(@RequestHeader("X-Authorization-Id") Long memberId,
                                                     @PathVariable("postId") Long postId) {

        List<CommentResponse> commentsByPostId = commentService.getCommentsByPostId(postId, memberId);

        return new RsData<>(
                "200",
                "댓글 조회 완료",
                commentsByPostId
        );
    }
}