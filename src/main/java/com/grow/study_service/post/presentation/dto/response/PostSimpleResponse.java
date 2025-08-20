package com.grow.study_service.post.presentation.dto.response;

import com.grow.study_service.post.domain.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostSimpleResponse {

    private Long postId;
    private Long boardId;
    private Long memberId;
    private String title;
    private LocalDateTime createdAt;

    public static PostSimpleResponse of(Post post) {
        return new PostSimpleResponse(
                post.getPostId(),
                post.getBoardId(),
                post.getMemberId(),
                post.getTitle(),
                post.getCreatedAt()
        );
    }
}
