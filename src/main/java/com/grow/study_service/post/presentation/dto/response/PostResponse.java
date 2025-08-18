package com.grow.study_service.post.presentation.dto.response;

import com.grow.study_service.post.domain.model.FileMeta;
import com.grow.study_service.post.domain.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 게시글 조회/응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long postId;
    private Long boardId;
    private Long memberId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private List<FileMetaResponse> files;

    public static PostResponse of(Post post, List<FileMeta> fileMetas) {
        List<FileMetaResponse> fileResponses = (fileMetas == null || fileMetas.isEmpty())
                ? Collections.emptyList() // 파일이 없을 경우 empty list 반환
                : fileMetas.stream()
                .map(m -> new FileMetaResponse(
                        m.getFileId(),
                        m.getOriginalName(),
                        m.getStoredName(),
                        m.getContentType(),
                        m.getSize(),
                        m.getPath()
                ))
                .toList();

        return new PostResponse(
                post.getPostId(),
                post.getBoardId(),
                post.getMemberId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                fileResponses
        );
    }
}