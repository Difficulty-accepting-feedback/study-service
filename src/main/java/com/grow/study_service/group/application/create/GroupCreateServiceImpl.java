package com.grow.study_service.group.application.create;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.group.domain.document.GroupDocument;
import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.group.domain.repository.GroupRepository;
import com.grow.study_service.group.infra.persistence.repository.elastic.GroupDocumentRepository;
import com.grow.study_service.group.presentation.dto.create.GroupCreateRequest;
import com.grow.study_service.groupmember.domain.enums.Role;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupCreateServiceImpl implements GroupCreateService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupDocumentRepository groupDocumentRepository;

    /**
     * 그룹 생성을 수행합니다.
     *
     * @param request  - 그룹 생성 요청 DTO 객체 (이름, 카테고리, 설명 등)
     * @param memberId - 생성자의 아이디 (그룹 리더)
     * @return 생성된 그룹의 ID
     */
    @Override
    @Transactional
    @Counted("group.create")
    public Long createGroup(GroupCreateRequest request, Long memberId) {
        log.info("[GROUP][CREATE][START] 생성자={} - 그룹 생성 시작", memberId);

        // 1. 같은 이름으로 생성된 그룹이 있는지 확인하기 (중복 방지)
        if (groupRepository.existsByGroupName(request.getName())) {
            throw new ServiceException(ErrorCode.GROUP_ALREADY_EXISTS);
        }

        // 2. 그룹 생성
        Group saved = groupRepository.save(createNewGroup(request));

        // 3. 도큐먼트 객체 생성 + 4. 엘라스틱 서치에도 반영되도록 함
        groupDocumentRepository.save(createNewDocument(saved));

        // 5. 그룹에 그룹 멤버를 추가, 리더로 지정
        groupMemberRepository.save(GroupMember.create(memberId, saved.getGroupId(), Role.LEADER));

        log.info("[GROUP][CREATE][END] 생성자={} - 그룹 생성 완료 groupId={}", memberId, saved.getGroupId());
        return saved.getGroupId();
    }

    private GroupDocument createNewDocument(Group saved) {
        return new GroupDocument(
                saved.getGroupId().toString(), // 원본 Long 타입 저장 -> String 타입으로 저장
                saved.getName(),
                saved.getDescription(),
                saved.getCategory().getDescription(),
                saved.getStartAt(),
                saved.getEndAt(),
                saved.getAmount(),
                saved.getViewCount(),
                saved.getPersonalityTag(),
                saved.getSkillTag().getDescription()
        );
    }

    @NotNull
    private Group createNewGroup(GroupCreateRequest request) {
        return Group.create(
                request.getName(),
                request.getCategory(),
                request.getDescription(),
                request.getPersonalityTag(),
                request.getSkillTag(),
                request.getAmount(),
                request.getEndAt()
        );
    }
}
