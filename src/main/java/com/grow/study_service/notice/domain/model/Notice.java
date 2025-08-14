package com.grow.study_service.notice.domain.model;

import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 그룹 공지사항 도메인.
 * <p>공지 생성/복원 시 필수 값 유효성 검증 수행</p>
 *
 * <ul>
 *   <li>{@link #create(Long, String, boolean)} - 신규 생성</li>
 *   <li>{@link #of(Long, Long, String, boolean)} - 조회 후 복원</li>
 * </ul>
 */
@Slf4j
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

    /**
     * 해당 공지사항이 특정 그룹에 속하는지 검증합니다.
     * <p>
     * 이 메서드는 공지사항 엔티티의 {@code groupId}와 전달받은 {@code expectedGroupId}를 비교하여
     * 다를 경우 {@link DomainException}을 발생시킵니다.
     *
     * @param expectedGroupId 검증 대상 그룹 ID
     *
     * @throws DomainException 공지사항이 지정한 그룹에 속하지 않는 경우
     *
     * @implNote 주로 서비스 계층에서, 리소스 접근 권한 검증 전후에 사용됩니다.
     */
    public void verifyBelongsToGroup(Long expectedGroupId) {
        if (!this.groupId.equals(expectedGroupId)) {
            log.warn("[NOTICE][DELETE][UNAUTHORIZED] groupId={}, noticeId={} - 그룹 ID와 공지사항 그룹 ID가 일치하지 않음",
                    expectedGroupId, noticeId);
            throw new DomainException(ErrorCode.GROUP_NOT_MATCH);
        }
    }
}
