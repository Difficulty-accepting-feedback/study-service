package com.grow.study_service.group.presentation.dto.create;

import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.enums.PersonalityTag;
import com.grow.study_service.group.domain.enums.SkillTag;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class GroupCreateRequest {

    /** 그룹의 카테고리. 예를 들어, 스터디 주제나 취미 등의 유형을 나타내는 열거형 값입니다. */
    @NotNull(message = "카테고리는 필수입니다.")
    private final Category category;

    /** 그룹의 종료 날짜와 시간. 그룹이 종료되기 전까지 유효합니다. */
    @NotNull(message = "종료 시간은 필수입니다.")
    @Future(message = "종료 시간은 현재 또는 미래여야 합니다.")
    private final LocalDate endAt;

    /** 그룹의 이름. 업데이트 가능하며, 비어 있지 않아야 합니다. */
    @NotBlank(message = "그룹 이름은 필수이며, 빈 문자열일 수 없습니다.")
    private String name;

    /** 그룹의 상세 설명. 업데이트 가능하며, 비어 있지 않아야 합니다. */
    @NotBlank(message = "상세 설명은 필수이며, 빈 문자열일 수 없습니다.")
    private String description;

    /** 멘토링 시, 가격 설정 값. (null 허용) */
    private int amount;

    /** 그룹의 성격/특성 관련 태그. (null 허용) */
    private PersonalityTag personalityTag;

    /** 기술/스킬 관련 태그. */
    @NotNull(message = "기술/스킬 태그는 필수입니다.")
    private SkillTag skillTag;

    // TODO 사전에 추가하고 싶은 사용자가 있는지 물어보기 -> 닉네임 or 이메일로 검색 + 추가
    private List<String> memberNicknames;
}