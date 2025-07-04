package com.grow.study_service.group.member.domain.repository;

import java.util.List;
import java.util.Optional;

import com.grow.study_service.group.member.domain.model.GroupMember;

public interface GroupMemberRepository {
	GroupMember save(GroupMember member);
	Optional<GroupMember> findById(Long groupMemberId);
	List<GroupMember> findByGroupId(Long groupId);
	void delete(GroupMember member);
}
