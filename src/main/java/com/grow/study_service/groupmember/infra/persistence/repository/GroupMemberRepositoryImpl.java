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

	/**
	 * 지정된 그룹에 특정 회원이 속해 있는지 여부를 확인한다.
	 * <p>
	 * DB에서 해당 그룹 ID와 회원 ID로 검색하여 존재 여부를 반환한다.
	 * </p>
	 *
	 * @param groupId  그룹 ID
	 * @param memberId 회원 ID
	 * @return {@code true} - 해당 회원이 그룹에 속한 경우,
	 *         {@code false} - 속하지 않은 경우
	 */
	@Override
	public boolean existsByGroupIdAndMemberId(Long groupId, Long memberId) {
		return groupMemberJpaRepository.existsByGroupIdAndMemberId(groupId, memberId);
	}

	@Override
	public boolean existsByMemberIdAndPostGroup(Long postId, Long memberId) {
		return groupMemberJpaRepository.existsByMemberIdAndPostGroup(postId, memberId);
	}
}
