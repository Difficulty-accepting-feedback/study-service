package com.grow.study_service.notice.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeUpdateRequest {

    /** 공지 ID (필수) */
    @NotNull(message = "공지 ID는 필수입니다.")
    private Long noticeId;

    /**
     * 공지 내용
     * <p>10자 이상, 500자 이하 입력 가능</p>
     */
    @NotBlank(message = "공지 내용은 필수입니다.")
    @Size(min = 10, max = 500, message = "공지 내용은 최소 10자 이상, 최대 500자 이하로 입력해야 합니다.")
    private String content;

    /**
     * 상단 고정 여부
     * <p>필수 값이며, true/false로 지정</p>
     */
    @NotNull(message = "상단 고정 여부를 지정해야 합니다.")
    private Boolean isPinned;
}
