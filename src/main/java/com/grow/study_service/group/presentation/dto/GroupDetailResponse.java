package com.grow.study_service.group.presentation.dto;

import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.enums.PersonalityTag;
import com.grow.study_service.group.domain.enums.SkillTag;
import com.grow.study_service.group.domain.model.Group;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupDetailResponse {

    private Long groupId;
    private String groupName;
    private String leaderNickname;
    private Category category;
    private String description;
    private int amount;
    private int viewCount;
    private int memberCount;
    private PersonalityTag personalityTag;
    private SkillTag skillTag;

    public static GroupDetailResponse of(Group group, int memberCount, String leaderNickname) {
        return GroupDetailResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getName())
                .leaderNickname(leaderNickname)
                .category(group.getCategory())
                .description(group.getDescription())
                .amount(group.getAmount())
                .viewCount(group.getViewCount())
                .memberCount(memberCount)
                .personalityTag(group.getPersonalityTag())
                .skillTag(group.getSkillTag())
                .build();
    }
}
