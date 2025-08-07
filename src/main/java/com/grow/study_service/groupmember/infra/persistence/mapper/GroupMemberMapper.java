package com.grow.study_service.groupmember.infra.persistence.mapper;

import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.infra.persistence.entity.GroupMemberJpaEntity;

import static com.grow.study_service.groupmember.infra.persistence.entity.GroupMemberJpaEntity.*;

/**
 * 그룹 멤버 매퍼 클래스.
 * 이 클래스는 그룹 멤버 도메인 모델(GroupMember)과 JPA 엔티티(GroupMemberJpaEntity) 간의 변환을 담당합니다.
 * 도메인 모델과 데이터베이스 엔티티 간 매핑을 통해 데이터의 일관성을 유지합니다.
 * 모든 메서드는 정적(static)으로 구현되어, 인스턴스 생성 없이 사용할 수 있습니다.
 *
 * @author sun
 * @version 1.0
 * @since 2025-08-07
 */
public class GroupMemberMapper {

	/**
	 * JPA 엔티티를 도메인 모델로 변환하는 메서드.
	 * 데이터베이스에서 조회된 엔티티를 도메인 객체로 매핑합니다.
	 *
	 * @param entity 변환할 GroupMemberJpaEntity 인스턴스. null이 아니어야 합니다.
	 * @return 변환된 GroupMember 도메인 모델 인스턴스.
	 */
	public static GroupMember toDomain(GroupMemberJpaEntity entity) {
		return GroupMember.of(
				entity.getId(),
				entity.getMemberId(),
				entity.getGroupId(),
				entity.getRole(),
				entity.getJoinedAt()
		);
	}

	/**
	 * 도메인 모델을 JPA 엔티티로 변환하는 메서드.
	 * 도메인 객체를 데이터베이스 저장을 위한 엔티티로 매핑합니다.
	 * 그룹 멤버 ID가 null이 아닌 경우 빌더에 설정합니다.
	 *
	 * @param domain 변환할 GroupMember 도메인 모델 인스턴스. null이 아니어야 합니다.
	 * @return 변환된 GroupMemberJpaEntity 인스턴스.
	 */

	public static GroupMemberJpaEntity toEntity(GroupMember domain) {
		GroupMemberJpaEntityBuilder builder = builder()
				.id(domain.getGroupMemberId())
				.memberId(domain.getMemberId())
				.groupId(domain.getGroupId())
				.role(domain.getRole())
				.joinedAt(domain.getJoinedAt());

		if (domain.getGroupId() != null) {
			builder.id(domain.getGroupMemberId());
		}

		return builder.build();
	}
}
