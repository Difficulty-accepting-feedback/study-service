package com.grow.study_service.board.comment.domain.repository;

import java.util.List;
import java.util.Optional;

import com.grow.study_service.board.comment.domain.model.Comment;

public interface CommentRepository {
	Comment save(Comment comment);
	Optional<Comment> findById(Long commentId);
	List<Comment> findByPostId(Long postId);
	void delete(Comment comment);
}
