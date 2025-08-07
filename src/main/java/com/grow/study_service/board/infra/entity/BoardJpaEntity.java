package com.grow.study_service.board.infra.entity;

import java.time.LocalDateTime;

import com.grow.study_service.board.domain.enums.BoardType;
import jakarta.persistence.*;
import lombok.*;

/**
 * 게시판 엔티티 클래스 (자료 공유, 공지사항, 과제 제출 용도 사용)
 */
@Entity
@Getter
@Builder
@Table(name = "board")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class BoardJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long groupId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	private BoardType boardType;

	@Column(nullable = false)
	private LocalDateTime createdAt;
}
