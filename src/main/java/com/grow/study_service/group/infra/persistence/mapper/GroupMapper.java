package com.grow.study_service.group.infra.persistence.mapper;

import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.group.infra.persistence.entity.GroupJpaEntity;
import com.grow.study_service.group.infra.persistence.entity.GroupJpaEntity.GroupJpaEntityBuilder;

/**
 * 그룹 매퍼 클래스.
 * 이 클래스는 그룹 도메인 모델(Group)과 JPA 엔티티(GroupJpaEntity) 간의 변환을 담당합니다.
 * 도메인 모델과 데이터베이스 엔티티 간 매핑을 통해 데이터의 일관성을 유지합니다.
 * 모든 메서드는 정적(static)으로 구현되어, 인스턴스 생성 없이 사용할 수 있습니다.
 *
 * @author sun
 * @version 1.0
 * @since 2025-08-07
 */
public class GroupMapper {

	/**
	 * JPA 엔티티를 도메인 모델로 변환하는 메서드.
	 * 데이터베이스에서 조회된 엔티티를 도메인 객체로 매핑합니다.
	 *
	 * @param entity 변환할 GroupJpaEntity 인스턴스. null이 아니어야 함.
	 * @return 변환된 Group 도메인 모델 인스턴스.
	 */
	public static Group toDomain(GroupJpaEntity entity) {
		return Group.of(
				entity.getId(),
				entity.getName(),
				entity.getCategory(),
				entity.getDescription(),
				entity.getCreatedAt()
		);
	}

	/**
	 * 도메인 모델을 JPA 엔티티로 변환하는 메서드.
	 * 도메인 객체를 데이터베이스 저장을 위한 엔티티로 매핑합니다.
	 * 그룹 ID가 null이 아닌 경우 빌더를 설정하여 기존 필드를 업데이트 합니다.
	 *
	 * @param group 변환할 Group 도메인 모델 인스턴스.
	 * @return 변환된 GroupJpaEntity 인스턴스.
	 */
	public static GroupJpaEntity toEntity(Group group) {
		GroupJpaEntityBuilder builder = GroupJpaEntity.builder()
				.name(group.getName())
				.category(group.getCategory())
				.description(group.getDescription())
				.createdAt(group.getCreatedAt());

		if (group.getGroupId() != null) {
			builder.id(group.getGroupId());
		}

        return builder.build();
	}
}
