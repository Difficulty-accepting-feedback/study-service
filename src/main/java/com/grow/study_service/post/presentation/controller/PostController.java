package com.grow.study_service.post.presentation.controller;

import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.post.application.find.PostFindService;
import com.grow.study_service.post.application.save.PostSaveService;
import com.grow.study_service.post.presentation.dto.request.PostSaveRequest;
import com.grow.study_service.post.presentation.dto.response.PostResponse;
import com.grow.study_service.post.presentation.dto.response.PostSimpleResponse;
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
public class PostController {

    private final PostSaveService postSaveService;
    private final PostFindService postFindService;

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

        PostResponse createdPost = postSaveService.createPost(memberId, request, files);

        return new RsData<>("201",
                "글 작성 완료",
                createdPost // 글 작성 성공 시 생성된 게시글 ID 반환 -> 바로 조회 가능하도록
        );
    }

    /**
     * 게시물 단건 조회 API
     * <p>
     * HTTP GET 요청을 통해 특정 게시물을 조회합니다.
     * <ol>
     *     <li>요청 헤더의 회원 ID(`X-Authorization-Id`)를 추출합니다.</li>
     *     <li>경로 변수의 게시물 ID를 사용하여 서비스 계층의 게시물 조회 메서드를 호출합니다.</li>
     *     <li>조회 결과(PostResponse)를 래핑한 {@link RsData} 객체로 변환하여 반환합니다.</li>
     * </ol>
     *
     * @param memberId 요청 헤더 `X-Authorization-Id`에 포함된 회원 ID (인증/권한 확인용)
     * @param postId   조회할 게시물의 ID (경로 변수)
     *
     * @return {@link RsData} 게시물 조회 결과.
     *         성공 시 상태 코드 "200", 메시지 "글 조회 완료", 게시물 응답 DTO를 포함합니다.
     *
     * @throws ServiceException 게시물이 존재하지 않거나 사용자가 게시판 멤버가 아닌 경우 발생 (Service 계층에서 전파됨)
     *
     * @implNote 컨트롤러는 단순히 입력을 추출하고 서비스 계층 호출 후 결과를 반환하는 역할만 수행합니다.
     *           예외 처리 및 권한 검증 책임은 서비스 계층(`postService.findById`)에 위임합니다.
     *
     * @see PostFindService#findById(Long, Long)
     * @see PostResponse
     * @see RsData
     */
    @GetMapping("/{postId}")
    public RsData<PostResponse> getPost(@RequestHeader("X-Authorization-Id") Long memberId,
                                        @PathVariable("postId") Long postId) {
        PostResponse response = postFindService.findById(postId, memberId);

        return new RsData<>("200",
                "글 조회 완료",
                response
        );
    }

    // 글 수정을 위한 API

    // 글 삭제를 위한 API

    // 목록 전체 조회를 위한 API
    @GetMapping("/list")
    public RsData<List<PostSimpleResponse>> getPostList(@RequestHeader("X-Authorization-Id") Long memberId,
                                                        @RequestParam("boardId") Long boardId) {

        List<PostSimpleResponse> response = postFindService.getPostList(memberId, boardId);

        return new RsData<>("200",
                "글 목록 조회 완료",
                response
        );
    }
}
