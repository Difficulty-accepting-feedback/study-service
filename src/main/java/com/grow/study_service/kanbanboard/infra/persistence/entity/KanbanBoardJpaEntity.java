package com.grow.study_service.kanbanboard.infra.persistence.entity;

import java.time.LocalDateTime;

import com.grow.study_service.kanbanboard.domain.enums.KanbanStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(
		name = "kanban_board",
		indexes = {
				// groupMemberId로 필터링 후 startDate로 범위 검색
				@Index(name = "idx_group_member_start_date", columnList = "groupMemberId, startDate")
		}
)
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