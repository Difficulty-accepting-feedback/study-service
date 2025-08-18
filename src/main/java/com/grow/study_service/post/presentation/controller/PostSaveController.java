package com.grow.study_service.post.presentation.controller;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.post.application.save.PostSaveService;
import com.grow.study_service.post.presentation.dto.request.PostSaveRequest;
import com.grow.study_service.post.presentation.dto.response.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostSaveController {

    private final PostSaveService postService;

    @Operation(
            summary = "게시글 생성",
            description = "회원 ID와 게시글 내용을 받아 새로운 게시글을 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "게시글이 성공적으로 생성됨")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    @PostMapping("/save")
    public RsData<PostResponse> createPost(@RequestHeader("X-Authorization-Id") Long memberId,
                                           @RequestPart("post") PostSaveRequest request,
                                           @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        PostResponse createdPost = postService.createPost(memberId, request, files);

        return new RsData<>("201",
                "글 작성 완료",
                createdPost
        );
    }

    // 글 수정을 위한 API

    // 글 삭제를 위한 API

    // 목록 전체 조회를 위한 API

    // 글 단건 조회를 위한 API
}
