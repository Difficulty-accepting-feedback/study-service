package com.grow.study_service.board.infra.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.grow.study_service.board.domain.model.Board;
import com.grow.study_service.board.domain.repository.BoardRepository;
import com.grow.study_service.board.infra.mapper.BoardMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {
	private final BoardJpaRepository boardJpaRepository;

	@Override
	public Board save(Board board) {
		return BoardMapper.toDomain(boardJpaRepository.save(BoardMapper.toEntity(board)));
	}

	@Override
	public Optional<Board> findById(Long boardId) {
		return boardJpaRepository.findById(boardId).map(BoardMapper::toDomain);
	}

	@Override
	public List<Board> findByGroupId(Long groupId) {
		return boardJpaRepository.findByGroupId(groupId).stream()
				.map(BoardMapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public void delete(Board board) {
		boardJpaRepository.delete(BoardMapper.toEntity(board));
	}
}
