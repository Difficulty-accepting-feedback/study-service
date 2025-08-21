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

    /**
     * [게시글 소유자 검증 메서드]
     * <p>
     * 현재 게시글의 소유자가 입력된 memberId와 일치하는지 확인합니다.
     * 필요한 경우 내부 처리 절차를 순서대로 기술할 수 있습니다.
     *
     * <ol>
     *     <li>memberId 비교</li>
     *     <li>불일치 시 예외 발생</li>
     * </ol>
     *
     * @param memberId 검증할 회원 ID
     *
     * @throws DomainException 소유자가 아닐 경우
     *
     * @see ErrorCode#NOT_AUTHORIZED_USER
     */
    public void validateMember(Long memberId) {
        if (this.memberId != memberId) {
            throw new DomainException(ErrorCode.NOT_AUTHORIZED_USER);
        }
    }

    /**
     * [게시글 업데이트 메서드 (변경 여부 확인)]
     * <p>
     * 제목과 내용이 기존과 다른 경우에만 업데이트하며, 업데이트 시간을 설정합니다.
     * 필요한 경우 내부 처리 절차를 순서대로 기술할 수 있습니다.
     *
     * <ol>
     *     <li>제목 변경 확인</li>
     *     <li>내용 변경 확인</li>
     *     <li>업데이트 시간 설정</li>
     * </ol>
     *
     * @param title 새로운 제목 (null 또는 빈 값 무시)
     * @param content 새로운 내용 (null 또는 빈 값 무시)
     *
     * @implNote 변경 여부 확인으로 불필요한 업데이트 방지
     */
    public void update(String title, String content) {
        if (!this.title.equals(title) && title != null && !title.isBlank()) {
            this.title = title;
        }

        if (!this.content.equals(content) && content != null && !content.isBlank()) {
            this.content = content;
        }

        this.updatedAt = LocalDateTime.now();
    }
}