package com.grow.study_service.group.infra.persistence.repository.query;

import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.infra.persistence.entity.QGroupJpaEntity;
import com.grow.study_service.group.presentation.dto.GroupSimpleResponse;
import com.grow.study_service.groupmember.infra.persistence.entity.QGroupMemberJpaEntity;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupQueryRepositoryImpl implements GroupQueryRepository {

    private final JPAQueryFactory factory;

    /**
     * 주어진 회원 ID와 카테고리를 기반으로, 해당 회원이 가입한 그룹 목록을 조회합니다.
     * 조회 결과는 지정된 카테고리에 속하는 그룹만 포함되며, 가입일(joinedAt) 기준으로 최신 순서로 정렬됩니다.
     * QueryDSL을 사용하여 GroupMemberJpaEntity와 GroupJpaEntity를 ID 기반으로 조인하며,
     * 결과를 GroupSimpleResponse DTO 리스트로 반환합니다.
     *
     * @param memberId 조회할 회원의 ID (필수, null 불가)
     * @param category 그룹을 필터링할 카테고리 (예: 스포츠, 학습 등)
     * @return GroupSimpleResponse 객체 리스트 (그룹 ID, 이름, 역할, 가입일 포함). 빈 리스트일 수 있음
     */
    public List<GroupSimpleResponse> findJoinedGroupsByMemberAndCategory(Long memberId, Category category) {
        QGroupMemberJpaEntity groupMember = QGroupMemberJpaEntity.groupMemberJpaEntity;
        QGroupJpaEntity group = QGroupJpaEntity.groupJpaEntity;

        List<Tuple> tupleList = factory.select(group.id, group.name, groupMember.role, groupMember.joinedAt)
                .from(groupMember)
                .join(group)
                .on(groupMember.groupId.eq(group.id)) // ID 기반 (GroupMember 테이블과 Group 테이블을 조인)
                .where(
                        groupMember.memberId.eq(memberId), // 멤버 ID가 같고
                        group.category.eq(category) // 카테고리가 같음
                )
                .orderBy(groupMember.joinedAt.desc()) // 최신 가입 순서 정렬
                .fetch();

        return tupleList.stream()
                .map(tuple -> new GroupSimpleResponse(
                        tuple.get(group.id),
                        tuple.get(group.name),
                        tuple.get(groupMember.role),
                        tuple.get(groupMember.joinedAt)
                ))
                .toList();
    }
}
