package com.grow.study_service.kanbanboard.infra.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.grow.study_service.kanbanboard.domain.model.KanbanBoard;
import com.grow.study_service.kanbanboard.domain.repository.KanbanBoardRepository;
import com.grow.study_service.kanbanboard.infra.persistence.entity.KanbanBoardJpaEntity;
import com.grow.study_service.kanbanboard.infra.persistence.mapper.KanbanBoardMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class KanbanBoardRepositoryImpl implements KanbanBoardRepository {

	private final KanbanBoardJpaRepository kanbanBoardJpaRepository;

	@Override
	public KanbanBoard save(KanbanBoard board) {
		KanbanBoardJpaEntity saved = kanbanBoardJpaRepository.save(KanbanBoardMapper.toEntity(board));
		return KanbanBoardMapper.toDomain(saved);
	}

	@Override
	public Optional<KanbanBoard> findById(Long toDoId) {
		return kanbanBoardJpaRepository.findById(toDoId)
			.map(KanbanBoardMapper::toDomain);
	}

	@Override
	public void delete(KanbanBoard board) {
		kanbanBoardJpaRepository.delete(KanbanBoardMapper.toEntity(board));
	}

	/**
	 * 그룹 멤버 ID를 기준으로 지정된 날짜 범위 내의 KanbanBoard 목록을 조회합니다.
	 * JPA 리포지토리를 통해 엔티티를 조회한 후, 도메인 객체로 매핑하여 반환합니다.
	 *
	 * @param groupMemberId 그룹 멤버 ID
	 * @param startDate 범위 시작 날짜/시간 (포함)
	 * @param endDate 범위 종료 날짜/시간 (포함)
	 * @return 해당 범위 내 KanbanBoard 도메인 객체 목록
	 */
	@Override
	public List<KanbanBoard> findByGroupMemberIdAndDateBetween(Long groupMemberId,
															   LocalDateTime startDate,
															   LocalDateTime endDate) {
		return kanbanBoardJpaRepository.findByGroupMemberIdAndDateBetween(groupMemberId, startDate, endDate)
				.stream()
				.map(KanbanBoardMapper::toDomain)
				.toList();
	}

/*	*//**
	 * 주어진 시간과 startDate가 정확히 동일한 KanbanBoard 목록을 반환합니다.
	 *
	 * @param date 비교할 시간
	 * @return 해당 startDate와 동일한 KanbanBoard 목록
	 *//*
	@Override
	public List<KanbanBoard> findAllByStartDateEquals(LocalDateTime date) {
		return kanbanBoardJpaRepository.findAllByStartDateEquals(date)
				.stream()
				.map(KanbanBoardMapper::toDomain)
				.toList();
	}*/
}