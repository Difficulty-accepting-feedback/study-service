package com.grow.study_service.comment.infra.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "comment")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 댓글 아이디

	@Column(nullable = false)
	private Long postId; // 게시글 아이디

	@Column(nullable = false)
	private Long memberId; // 댓글 작성 회원 아이디

	@Column
	private Long parentId; // 부모 댓글 아이디

	@Column(nullable = false)
	private String content; // 댓글 내용

	@Column(nullable = false)
	private LocalDateTime createdAt; // 댓글 작성 시간

	@Column
	private LocalDateTime updatedAt; // 댓글 수정 시간

	@Version
	private Long version; // Optimistic Lock
}