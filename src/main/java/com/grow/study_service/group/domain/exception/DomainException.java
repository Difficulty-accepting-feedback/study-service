package com.grow.study_service.group.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
}
