package com.grow.study_service.board.infra.mapper;

import java.util.Collections;

import com.grow.study_service.board.domain.model.Board;
import com.grow.study_service.board.infra.entity.BoardJpaEntity;

public class BoardMapper {
	public static Board toDomain(BoardJpaEntity e) {
		return Board.of(
			e.getId(),
			e.getGroupId(),
			e.getName(),
			e.getDescription(),
			e.getCreatedAt(),
			Collections.emptyList()
		);
	}

	public static BoardJpaEntity toEntity(Board d) {
		return BoardJpaEntity.builder()
				.id(d.getBoardId())
				.groupId(d.getGroupId())
				.name(d.getName())
				.description(d.getDescription())
				.createdAt(d.getCreatedAt())
				.build();
	}
}
