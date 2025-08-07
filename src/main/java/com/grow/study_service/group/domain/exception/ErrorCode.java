package com.grow.study_service.group.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    GROUP_NAME_IS_EMPTY("400", "그룹 이름은 비어있을 수 없습니다."),
    GROUP_DESCRIPTION_IS_EMPTY("400", "그룹 설명은 비어있을 수 없습니다." ),
    GROUP_ID_IS_EMPTY("400", "그룹 아이디는 비어있을 수 없습니다." ),
    CATEGORY_IS_EMPTY("400", "카테고리는 비어있을 수 없습니다." ),

    GROUP_MEMBER_MEMBER_ID_IS_EMPTY("400", "그룹에 속해있는 멤버의 아이디는 비어있을 수 없습니다." ),
    GROUP_MEMBER_GROUP_ID_IS_EMPTY("400", "그룹 멤버가 속해있는 그룹의 아이디는 비어있을 수 없습니다." ),
    GROUP_MEMBER_ROLE_IS_EMPTY("400", "그룹 멤버의 역할은 비어있을 수 없습니다." ),
    GROUP_MEMBER_JOINED_AT_IS_EMPTY("400", "그룹 멤버가 가입한 시각은 비어있을 수 없습니다." ),
    GROUP_MEMBER_ID_IS_EMPTY("400", "그룹 멤버 아이디는 비어있을 수 없습니다." ),

    BOARD_NAME_IS_EMPTY("400", "게시판 이름은 비어있을 수 없습니다." ),
    BOARD_DESCRIPTION_IS_EMPTY("400", "게시판 설명은 비어있을 수 없습니다." ),
    BOARD_TYPE_IS_EMPTY("400", "게시판 타입은 비어있을 수 없습니다." ),
    BOARD_ID_IS_EMPTY("400", "게시판 아이디는 비어있을 수 없습니다." ),
    BOARD_CREATED_AT_IS_EMPTY("400", "게시판 생성 시각은 비어있을 수 없습니다." ),
    ;

    private final String code;
    private final String message;
}
