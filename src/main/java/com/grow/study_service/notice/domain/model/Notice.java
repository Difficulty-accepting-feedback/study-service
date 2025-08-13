package com.grow.study_service.notice.domain.model;

import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 그룹 공지사항 도메인.
 * <p>공지 생성/복원 시 필수 값 유효성 검증 수행</p>
 *
 * <ul>
 *   <li>{@link #create(Long, String, boolean)} - 신규 생성</li>
 *   <li>{@link #of(Long, Long, String, boolean)} - 조회 후 복원</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public class Notice {

    private Long noticeId;       // 공지사항 ID
    private final Long groupId;  // 그룹 ID
    private String content;      // 내용
    private boolean isPinned;    // 메인 고정 여부

    /**
     * 신규 공지 생성
     * @throws DomainException groupId·content 유효하지 않으면 예외
     */
    public static Notice create(Long groupId, String content, boolean isPinned) {
        verifyParameters(groupId, content);
        return new Notice(null, groupId, content, isPinned);
    }

    /** groupId, content 유효성 검증 */
    private static void verifyParameters(Long groupId, String content) {
        if (groupId == null || groupId <= 0L) {
            throw new DomainException(ErrorCode.GROUP_ID_IS_EMPTY);
        }
        if (content == null || content.isBlank()) {
            throw new DomainException(ErrorCode.NOTICE_CONTENT_IS_EMPTY);
        }
    }

    /**
     * 기존 공지 복원
     * @throws DomainException noticeId·groupId·content 유효하지 않으면 예외
     */
    public static Notice of(Long noticeId, Long groupId, String content, boolean isPinned) {
        verifyParameters(groupId, content);
        if (noticeId == null || noticeId <= 0L) {
            throw new DomainException(ErrorCode.NOTICE_ID_IS_EMPTY);
        }
        return new Notice(noticeId, groupId, content, isPinned);
    }
}
