package com.grow.study_service.group.presentation.dto.join;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinConfirmRequest {

    /**
     * 가입 요청을 전송한 그룹의 ID
     */
    @NotNull
    private final Long groupId;

    /**
     * 가입 요청을 전송했던 회원의 ID
     */
    @NotNull
    private final Long memberId;
}
