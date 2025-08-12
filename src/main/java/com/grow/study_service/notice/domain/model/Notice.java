package com.grow.study_service.notice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Notice {

    private Long noticeId; // 공지사항 아이디

    private final Long groupId; // 그룹 아이디

    private String content; // 내용

    private boolean isPinned; // 메인 등록 여부

    // 생성할 때
    public static Notice create(Long groupId,
                                String content) {
        return new Notice(
                null, // 공지사항 아이디는 자동 생성
                groupId,
                content,
                false // 메인 등록 여부는 기본 false로 설정
        );
    }

    // 조회할 때
    public static Notice of(Long noticeId,
                            Long groupId,
                            String content,
                            boolean isPinned) {
        return new Notice(
                noticeId,
                groupId,
                content,
                isPinned
        );
    }
}
