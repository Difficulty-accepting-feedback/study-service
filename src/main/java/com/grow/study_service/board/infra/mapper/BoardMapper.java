package com.grow.study_service.board.infra.mapper;

import com.grow.study_service.board.domain.model.Board;
import com.grow.study_service.board.infra.entity.BoardJpaEntity;

import static com.grow.study_service.board.infra.entity.BoardJpaEntity.*;

/**
 * 게시판 매퍼 클래스.
 * 이 클래스는 게시판 도메인 모델(Board)과 JPA 엔티티(BoardJpaEntity) 간의 변환을 담당합니다.
 * 도메인 모델과 데이터베이스 엔티티 간 매핑을 통해 데이터의 일관성을 유지합니다.
 * 모든 메서드는 정적(static)으로 구현되어, 인스턴스 생성 없이 사용할 수 있습니다.
 *
 * @author sun
 * @version 1.0
 * @since 2025-08-07
 */
public class BoardMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환하는 메서드.
     * 데이터베이스에서 조회된 엔티티를 도메인 객체로 매핑합니다.
     *
     * @param entity 변환할 BoardJpaEntity 인스턴스. null이 아니어야 합니다.
     * @return 변환된 Board 도메인 모델 인스턴스.
     */
    public static Board toDomain(BoardJpaEntity entity) {
        return Board.of(
                entity.getId(),
                entity.getGroupId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getBoardType()
        );
    }

    /**
     * 도메인 모델을 JPA 엔티티로 변환하는 메서드.
     * 도메인 객체를 데이터베이스 저장을 위한 엔티티로 매핑합니다.
     * 게시판 ID가 null이 아닌 경우 빌더에 설정합니다.
     *
     * @param domain 변환할 Board 도메인 모델 인스턴스. null이 아니어야 합니다.
     * @return 변환된 BoardJpaEntity 인스턴스.
     */
    public static BoardJpaEntity toEntity(Board domain) {
        BoardJpaEntityBuilder builder = builder()
                .groupId(domain.getGroupId())
                .name(domain.getName())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .boardType(domain.getBoardType());

        if (domain.getBoardId() != null) {
            builder.id(domain.getBoardId());
        }

        return builder.build();
    }
}
