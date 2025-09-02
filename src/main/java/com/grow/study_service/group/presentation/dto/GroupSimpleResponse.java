package com.grow.study_service.group.presentation.dto;

import com.grow.study_service.groupmember.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GroupSimpleResponse {

    private Long groupId; // 그룹 아이디
    private String groupName; // 그룹 이름
    private Role role; // 그룹 내부에서의 역할 (리더, 멤버)
    private LocalDateTime joinedAt; // 그룹 가입 시기
}
