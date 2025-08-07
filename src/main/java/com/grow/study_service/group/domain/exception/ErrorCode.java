package com.grow.study_service.group.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    GROUP_NAME_IS_EMPTY("400", "그룹 이름은 비어있을 수 없습니다."),
    GROUP_DESCRIPTION_IS_EMPTY("400", "그룹 설명은 비어있을 수 없습니다." ),
    CATEGORY_IS_EMPTY("400", "카테고리는 비어있을 수 없습니다." );

    private final String code;
    private final String message;
}
