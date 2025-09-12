package com.grow.study_service.kanbanboard.infra.persistence.mapper;

import com.grow.study_service.kanbanboard.domain.model.KanbanBoard;
import com.grow.study_service.kanbanboard.infra.persistence.entity.KanbanBoardJpaEntity;
import com.grow.study_service.kanbanboard.infra.persistence.entity.KanbanBoardJpaEntity.KanbanBoardJpaEntityBuilder;

public class KanbanBoardMapper {

    public static KanbanBoard toDomain(KanbanBoardJpaEntity e) {
        return KanbanBoard.of(
                e.getToDoId(),
                e.getGroupMemberId(),
                e.getToDoContent(),
                e.getIsCompleted(),
                e.getStartDate(),
                e.getEndDate()
        );
    }

    public static KanbanBoardJpaEntity toEntity(KanbanBoard domain) {
        KanbanBoardJpaEntityBuilder builder = KanbanBoardJpaEntity.builder()
                .toDoId(domain.getKanbanId())
                .groupMemberId(domain.getGroupMemberId())
                .toDoContent(domain.getContent())
                .isCompleted(domain.getStatus())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate());

        if (domain.getKanbanId() != null) {
            builder.toDoId(domain.getKanbanId());
        }

        return builder.build();
    }
}