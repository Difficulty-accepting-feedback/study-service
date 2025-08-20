package com.grow.study_service.post.domain.model;

import java.time.LocalDateTime;
import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post {

    private final Long postId;
    private final Long boardId;
    private final Long memberId;
    private final LocalDateTime createdAt;
    private String title;
    private String content;
    private LocalDateTime updatedAt;

    /**
     * 게시글을 생성하는 메서드
     */
    public static Post create(Long boardId, Long memberId, String title, String content) {
        if (title == null || title.isBlank()) {
            throw new DomainException(ErrorCode.TITLE_IS_EMPTY);
        }
        if (content == null || content.isBlank()) {
            throw new DomainException(ErrorCode.CONTENT_IS_EMPTY);
        }

        return new Post(
                null,
                boardId,
                memberId,
                LocalDateTime.now(),
                title,
                content,
                null
        );
    }

    /**
     * 게시글 제목과 내용을 업데이트하는 메서드
     * TODO 내용이 같은지 확인하고, 다른 경우에만 업데이트 할 수 있도록 변경
     */
    public void update(String title, String content, LocalDateTime now) {
        if (title == null || title.isBlank()) {
            throw new DomainException(ErrorCode.TITLE_IS_EMPTY);
        }
        if (content == null || content.isBlank()) {
            throw new DomainException(ErrorCode.CONTENT_IS_EMPTY);
        }

        this.title = title;
        this.content = content;
        this.updatedAt = now;
    }

    /**
     * 조회용 메서드
     */
    public static Post of(Long postId,
                          Long boardId,
                          Long memberId,
                          String title,
                          String content,
                          LocalDateTime createdAt,
                          LocalDateTime updatedAt) {
        return new Post(
                postId,
                boardId,
                memberId,
                createdAt,
                title,
                content,
                updatedAt
        );
    }
}