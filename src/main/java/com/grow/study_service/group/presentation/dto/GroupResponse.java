package com.grow.study_service.group.presentation.dto;

import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.enums.PersonalityTag;
import com.grow.study_service.group.domain.enums.SkillTag;
import com.grow.study_service.group.domain.model.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// TODO 그룹장의 이름을 같이 넘길 것
@Getter
@Builder
@AllArgsConstructor
public class GroupResponse {

    private Long groupId;
    private String groupName;
    private Category category;
    private String description;
    private int amount;
    private PersonalityTag personalityTag;
    private SkillTag skillTag;

    public static GroupResponse of(Group group) {
        return GroupResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getName())
                .category(group.getCategory())
                .amount(group.getAmount())
                .description(group.getDescription())
                .personalityTag(group.getPersonalityTag())
                .skillTag(group.getSkillTag())
                .build();
    }
}
