package com.grow.study_service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 📌 1. 그룹(Group) 관련
     */
    GROUP_NAME_IS_EMPTY("400", "그룹 이름은 비어있을 수 없습니다."),
    GROUP_DESCRIPTION_IS_EMPTY("400", "그룹 설명은 비어있을 수 없습니다."),
    GROUP_ID_IS_EMPTY("400", "그룹 아이디는 비어있을 수 없습니다."),
    CATEGORY_IS_EMPTY("400", "카테고리는 비어있을 수 없습니다."),
    GROUP_LEADER_REQUIRED("403", "이 기능은 그룹장 권한이 있는 사용자만 사용할 수 있습니다. 그룹장 권한 요청 후 다시 시도해 주세요."),

    /**
     * 📌 2. 그룹 멤버(Group Member) 관련
     */
    GROUP_MEMBER_MEMBER_ID_IS_EMPTY("400", "그룹에 속해있는 멤버의 아이디는 비어있을 수 없습니다."),
    GROUP_MEMBER_GROUP_ID_IS_EMPTY("400", "그룹 멤버가 속해있는 그룹의 아이디는 비어있을 수 없습니다."),
    GROUP_MEMBER_ROLE_IS_EMPTY("400", "그룹 멤버의 역할은 비어있을 수 없습니다."),
    GROUP_MEMBER_JOINED_AT_IS_EMPTY("400", "그룹 멤버가 가입한 시각은 비어있을 수 없습니다."),
    GROUP_MEMBER_ID_IS_EMPTY("400", "그룹 멤버 아이디는 비어있을 수 없습니다."),
    GROUP_MEMBER_NOT_FOUND("404", "그룹 멤버를 찾을 수 없습니다."),

    /**
     * 📌 3. 게시판(Board) 관련
     */
    BOARD_NAME_IS_EMPTY("400", "게시판 이름은 비어있을 수 없습니다."),
    BOARD_DESCRIPTION_IS_EMPTY("400", "게시판 설명은 비어있을 수 없습니다."),
    BOARD_TYPE_IS_EMPTY("400", "게시판 타입은 비어있을 수 없습니다."),
    BOARD_ID_IS_EMPTY("400", "게시판 아이디는 비어있을 수 없습니다."),
    BOARD_CREATED_AT_IS_EMPTY("400", "게시판 생성 시각은 비어있을 수 없습니다."),

    /**
     * 📌 4. 공지사항(Notice) 관련
     */
    NOTICE_CONTENT_IS_EMPTY("400", "공지 내용은 비어있을 수 없습니다."),
    NOTICE_ID_IS_EMPTY("400", "공지사항 아이디는 비어있을 수 없습니다." ),;

    private final String code;
    private final String message;
}
