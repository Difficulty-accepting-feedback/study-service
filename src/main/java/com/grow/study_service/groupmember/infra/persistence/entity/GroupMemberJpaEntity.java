package com.grow.study_service.groupmember.infra.persistence.entity;

import java.time.LocalDateTime;

import com.grow.study_service.groupmember.domain.enums.Role;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "group_member")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class GroupMemberJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	private Long groupId;

	@Enumerated(EnumType.STRING)
	private Role role;

	private LocalDateTime joinedAt;

	@Version
	private Long version;
}
