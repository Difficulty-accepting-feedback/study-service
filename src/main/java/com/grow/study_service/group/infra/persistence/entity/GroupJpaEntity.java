package com.grow.study_service.group.infra.persistence.entity;

import java.time.LocalDate;

import com.grow.study_service.group.domain.enums.Category;

import com.grow.study_service.group.domain.enums.PersonalityTag;
import com.grow.study_service.group.domain.enums.SkillTag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "groups")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class GroupJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Enumerated(EnumType.STRING)
	private Category category;

	@Column(columnDefinition = "TEXT")
	private String description;

	private int amount; // 멘토링 설정 값 (다른 항목에선 0 유지)

	private int viewCount; // 조회수

	@Enumerated(EnumType.STRING)
	private PersonalityTag personalityTag;

	@Enumerated(EnumType.STRING)
	private SkillTag skillTag;

	private LocalDate startAt; // 시작 날짜

	private LocalDate endAt; // 종료 날짜

	@Version
	private Long version; // 낙관적 락
}
