package com.grow.study_service.groupmember.domain.repository;

import java.util.Optional;

import com.grow.study_service.groupmember.domain.model.GroupMember;
import jakarta.validation.constraints.NotNull;

public interface GroupMemberRepository {
	GroupMember save(GroupMember member);
	Optional<GroupMember> findById(Long groupMemberId);
	void delete(GroupMember member);
	Optional<GroupMember> findGroupMemberByMemberIdAndGroupId(Long memberId, Long groupId);
	boolean existsByGroupIdAndMemberId(Long groupId, Long memberId);
	boolean existsByMemberIdAndPostGroup(Long postId, Long memberId);
    int findMemberCountByGroupId(Long groupId);
	Optional<GroupMember> findByGroupIdAndLeader(Long groupId);
	boolean isLeader(Long groupId, Long memberId);
	boolean existsByMemberIdAndGroupId(Long memberId, Long groupId);
	Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, Long memberId);
}
