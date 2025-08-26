package com.grow.study_service.comment.domain.repository;

import java.util.List;
import java.util.Optional;

import com.grow.study_service.comment.domain.model.Comment;

public interface CommentRepository {
	Comment save(Comment comment);
	Optional<Comment> findById(Long commentId);
	List<Comment> findByPostId(Long postId);
	void delete(Comment comment);
	// 중복 확인: postId, memberId, content가 동일한 댓글 존재 여부
	boolean existsByPostIdAndMemberIdAndContent(Long postId, Long memberId, String content);
	// 모든 댓글 가져오기: parentId 오름차순, createdAt 오름차순으로 조회
	List<Comment> getAllComments(Long postId);
}
