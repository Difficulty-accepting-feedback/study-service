package com.grow.study_service.groupmember.infra.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import com.grow.study_service.groupmember.infra.persistence.mapper.GroupMemberMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GroupMemberRepositoryImpl implements GroupMemberRepository {

	private final GroupMemberJpaRepository groupMemberJpaRepository;

	@Override
	public GroupMember save(GroupMember groupMember) {
		return GroupMemberMapper.toDomain(
			groupMemberJpaRepository.save(GroupMemberMapper.toEntity(groupMember))
		);
	}

	@Override
	public Optional<GroupMember> findById(Long groupMemberId) {
		return groupMemberJpaRepository.findById(groupMemberId)
			.map(GroupMemberMapper::toDomain);
	}

	@Override
	public List<GroupMember> findByGroupId(Long groupId) {
		return groupMemberJpaRepository.findByGroupId(groupId)
			.stream()
			.map(GroupMemberMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public void delete(GroupMember member) {
		groupMemberJpaRepository.delete(GroupMemberMapper.toEntity(member));
	}
}
