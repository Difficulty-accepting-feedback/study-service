package com.grow.study_service.common.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private Integer status;
    private String errorCode; // 에러 코드
    private String message;
    private List<String> error; // validation 오류 목록
    private String path; // 에러 발생 경로
    private LocalDateTime timestamp;

    public ErrorResponse(Integer status,
                         String errorCode,
                         String message,
                         String path,
                         LocalDateTime timestamp
    ) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }
}
