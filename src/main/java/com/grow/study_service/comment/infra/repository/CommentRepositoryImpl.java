package com.grow.study_service.comment.infra.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.grow.study_service.comment.domain.model.Comment;
import com.grow.study_service.comment.infra.mapper.CommentMapper;
import com.grow.study_service.comment.domain.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
	private final CommentJpaRepository commentJpaRepository;

	@Override
	public Comment save(Comment comment) {
		return CommentMapper.toDomain(commentJpaRepository.save(CommentMapper.toEntity(comment)));
	}

	@Override
	public Optional<Comment> findById(Long commentId) {
		return commentJpaRepository.findById(commentId).map(CommentMapper::toDomain);
	}

	@Override
	public List<Comment> findByPostId(Long postId) {
		return commentJpaRepository.findByPostId(postId).stream()
			.map(CommentMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public void delete(Comment comment) {
		commentJpaRepository.delete(CommentMapper.toEntity(comment));
	}
}