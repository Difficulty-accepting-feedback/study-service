package com.grow.study_service.notice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponse {

    /** 공지사항 ID */
    private Long noticeId;

    /** 공지 내용 */
    private String content;

    /** 상단 고정 여부 */
    private boolean isPinned;
}