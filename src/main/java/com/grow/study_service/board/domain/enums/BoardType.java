package com.grow.study_service.board.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardType {
    // 자료 공유 게시판
    DATA_SHARING("자료 공유 게시판"),

    // 과제 제출 게시판
    ASSIGNMENT_SUBMISSION("과제 제출 게시판"),

    // 공지사항 게시판
    NOTICE("공지사항 게시판"),

    // 커스텀 게시판
    CUSTOM("커스텀 게시판");

    private final String description;
}
