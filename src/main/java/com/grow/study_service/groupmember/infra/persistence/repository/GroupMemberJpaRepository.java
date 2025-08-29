package com.grow.study_service.groupmember.infra.persistence.repository;

import java.util.List;
import java.util.Optional;

import com.grow.study_service.groupmember.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.groupmember.infra.persistence.entity.GroupMemberJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupMemberJpaRepository
	extends JpaRepository<GroupMemberJpaEntity, Long> {
	Optional<GroupMemberJpaEntity> findByMemberIdAndGroupId(Long memberId, Long groupId);
	boolean existsByGroupIdAndMemberId(Long groupId, Long memberId);

	/**
	 * 주어진 postId에 해당하는 포스트가 속한 그룹에 memberId의 멤버가 속해 있는지 확인합니다.
	 *
	 * 이 메서드는 GroupMemberJpaEntity, BoardJpaEntity, PostJpaEntity를 조인하여
	 * 해당 포스트의 그룹 ID를 통해 멤버의 존재 여부를 검사합니다.
	 * 존재할 경우 true, 그렇지 않을 경우 false를 반환합니다.
	 *
	 * @param postId 확인할 포스트의 ID
	 * @param memberId 확인할 멤버의 ID
	 * @return 멤버가 해당 포스트의 그룹에 속해 있는지 여부 (true/false)
	 */
	@Query("select case when (count(gm) > 0) then true else false end " + // count(gm) > 0 = boolean 타입으로 바꾸기 위해
			"from GroupMemberJpaEntity gm " +
			"join BoardJpaEntity b on gm.groupId = b.groupId " +
			"join PostJpaEntity p on p.boardId = b.id " +
			"where p.id = :postId and gm.memberId = :memberId") // 주어진 postId와 memberId로 필터링
	boolean existsByMemberIdAndPostGroup(@Param("postId") Long postId,
										 @Param("memberId") Long memberId);

    long countByGroupId(Long groupId);

    List<GroupMemberJpaEntity> findByGroupIdAndRole(Long groupId, Role role);
}
