package com.grow.study_service.post.domain.repository;

import java.util.List;
import java.util.Optional;

import com.grow.study_service.post.domain.model.Post;

public interface PostRepository {
	Post save(Post post);
	Optional<Post> findById(Long postId);
	List<Post> findByBoardId(Long boardId);
	void delete(Post post);
}
