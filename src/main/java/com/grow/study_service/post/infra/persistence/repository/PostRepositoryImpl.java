package com.grow.study_service.post.infra.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.PostRepository;
import com.grow.study_service.post.infra.persistence.mapper.PostMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
	private final PostJpaRepository postJpaRepository;

	@Override
	public Post save(Post post) {
		return PostMapper.toDomain(postJpaRepository.save(PostMapper.toEntity(post)));
	}

	@Override
	public Optional<Post> findById(Long postId) {
		return postJpaRepository.findById(postId).map(PostMapper::toDomain);
	}

	@Override
	public List<Post> findByBoardId(Long boardId) {
		return postJpaRepository.findByBoardId(boardId).stream()
			.map(PostMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public void delete(Post post) {
		postJpaRepository.delete(PostMapper.toEntity(post));
	}
}