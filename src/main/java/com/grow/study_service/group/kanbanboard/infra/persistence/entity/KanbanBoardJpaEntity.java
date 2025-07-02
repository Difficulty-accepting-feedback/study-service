package com.grow.study_service.group.kanbanboard.infra.persistence.entity;

import java.time.LocalDateTime;

import com.grow.study_service.group.kanbanboard.domain.enums.KanbanStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "kanban_board")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class KanbanBoardJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long toDoId;

	@Column(nullable = false)
	private Long groupMemberId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String toDoContent;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private KanbanStatus isCompleted;

	@Column
	private LocalDateTime startDate;

	@Column
	private LocalDateTime endDate;
}