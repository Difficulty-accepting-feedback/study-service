package com.grow.study_service.board.domain.repository;

import java.util.List;
import java.util.Optional;

import com.grow.study_service.board.domain.model.Board;

public interface BoardRepository {
	Board save(Board board);
	Optional<Board> findById(Long boardId);
	List<Board> findByGroupId(Long groupId);
	void delete(Board board);
}
