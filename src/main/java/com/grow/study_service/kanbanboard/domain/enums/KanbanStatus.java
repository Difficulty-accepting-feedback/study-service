package com.grow.study_service.kanbanboard.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KanbanStatus {
	READY("시작 전"),
	IN_PROGRESS("진행 중"),
	DONE("완료");

	private final String description;
}
