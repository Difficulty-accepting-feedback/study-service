package com.grow.study_service.groupmember.domain.repository;

import java.util.Optional;

import com.grow.study_service.groupmember.domain.model.GroupMember;

public interface GroupMemberRepository {
	GroupMember save(GroupMember member);
	Optional<GroupMember> findById(Long groupMemberId);
	void delete(GroupMember member);
	Optional<GroupMember> findGroupMemberByMemberIdAndGroupId(Long memberId, Long groupId);
	boolean existsByGroupIdAndMemberId(Long groupId, Long memberId);
	boolean existsByMemberIdAndPostGroup(Long postId, Long memberId);
}
