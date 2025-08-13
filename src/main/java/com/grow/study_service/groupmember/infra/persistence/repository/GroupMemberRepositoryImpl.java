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

	/**
	 * 주어진 회원 ID와 그룹 ID를 기반으로 {@link GroupMember} 도메인 객체를 조회한다.
	 * <p>
	 * 내부적으로 JPA 레포지토리 {@code findByMemberIdAndGroupId} 메서드를 호출하여
	 * DB에서 엔티티를 조회한 뒤, 이를 도메인 객체로 변환한다.
	 * </p>
	 *
	 * @param memberId 조회할 그룹 멤버의 회원 ID
	 * @param groupId  조회할 그룹의 ID
	 * @return 일치하는 {@link GroupMember} 도메인 객체를 담은 {@link Optional}
	 *         (조회 결과가 없으면 {@link Optional#empty()} 반환)
	 */
	@Override
	public Optional<GroupMember> findGroupMemberByMemberIdAndGroupId(Long memberId, Long groupId) {
		return groupMemberJpaRepository.findByMemberIdAndGroupId(memberId, groupId)
				.map(GroupMemberMapper::toDomain);
	}
}
