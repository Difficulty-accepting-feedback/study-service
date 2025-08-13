package com.grow.study_service.notice.application.service;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import com.grow.study_service.notice.domain.model.Notice;
import com.grow.study_service.notice.domain.repository.NoticeRepository;
import com.grow.study_service.notice.presentation.dto.NoticeResponse;
import com.grow.study_service.notice.presentation.dto.NoticeSaveRequest;
import com.grow.study_service.notice.presentation.dto.NoticeUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 공지사항 서비스 구현체
 *
 * <p>{@link NoticeService} 인터페이스를 구현하며,
 * 공지사항 생성/저장 등의 비즈니스 로직을 처리한다.</p>
 *
 * @author sun
 * @see NoticeService
 * @since 1.0 (2025-08-13)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    /**
     * 공지사항 데이터 저장소
     */
    private final NoticeRepository noticeRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public void saveNotice(Long memberId, NoticeSaveRequest request) {
        log.info("[NOTICE][SAVE][START] memberId={}, contentLength={} - 공지사항 저장 요청 시작",
                memberId, request.getContent().length());

        // 권한 검증
        verifyAuthentification(memberId, request.getGroupId());

        // 저장 처리
        Notice notice = Notice.create(request.getGroupId(), request.getContent(), request.getIsPinned());
        noticeRepository.save(notice);

        log.info("[NOTICE][SAVE][END] memberId={}, groupId={}, noticeId={} - 공지사항 저장 성공",
                memberId, request.getGroupId(), notice.getNoticeId());
    }

    /**
     * 지정된 그룹의 공지사항들을 일괄 업데이트한다.
     * <p>
     * 1. 요청 회원이 해당 그룹의 그룹장인지 권한 검증<br>
     * 2. 권한이 있으면 요청 목록의 각 공지사항을 순회하며 내용·고정 여부를 수정<br>
     * 3. 수정된 건수를 로그에 기록
     * </p>
     *
     * @param groupId  수정할 그룹 ID
     * @param memberId 수정 요청 회원 ID
     * @param requests 수정할 공지사항 요청 목록
     * @throws ServiceException 그룹장 권한이 없거나, ID에 해당하는 공지가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public void updateNotices(Long groupId,
                              Long memberId,
                              List<NoticeUpdateRequest> requests) {
        log.info("[NOTICE][UPDATE][START] groupId={}, memberId={}, requestCount={} - 공지사항 업데이트 시작",
                groupId, memberId, requests.size());

        // 임시 저장용 공지사항 목록
        List<Notice> notices = new ArrayList<>();

        verifyAuthentification(memberId, groupId);

        for (NoticeUpdateRequest n : requests) {
            log.info("[Notice Save] 공지사항 저장: 내용={}", n.getContent());
            notices.add(
                    new Notice(
                            n.getNoticeId(),
                            groupId,
                            n.getContent(),
                            n.getIsPinned()
                    )
            );
        }

        noticeRepository.saveAll(notices);

        log.info("[NOTICE][UPDATE][END] groupId={}, memberId={}, updatedCount={} - 공지사항 업데이트 완료",
                groupId, memberId, requests.size());
    }

    /**
     * 특정 그룹의 공지사항 목록을 조회한다.
     * <p>
     * 1. 요청 회원이 해당 그룹에 속하는지 권한 검증
     * 2. 그룹 ID로 공지사항 조회
     * 3. 엔티티를 {@link NoticeResponse} DTO로 변환 후 반환
     * </p>
     *
     * @param groupId  조회할 그룹 ID
     * @param memberId 요청 회원 ID
     * @return 해당 그룹의 공지사항 목록
     * @throws ServiceException 요청 회원이 그룹에 속하지 않은 경우
     *                           ({@link ErrorCode#MEMBER_NOT_IN_GROUP})
     */
    @Override
    @Transactional(readOnly = true)
    public List<NoticeResponse> getNotices(Long groupId, Long memberId) {
        log.info("[NOTICE][GET][START] groupId={}, memberId={} - 공지사항 조회 요청 시작", groupId, memberId);

        // 1. 권한 검사
        boolean isMember = groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberId);
        if (!isMember) {
            log.warn("[NOTICE][GET][UNAUTHORIZED] groupId={}, memberId={} - 그룹 멤버 아님", groupId, memberId);
            throw new ServiceException(ErrorCode.MEMBER_NOT_IN_GROUP);
        }

        // 2. 공지사항 조회
        List<Notice> noticeList = noticeRepository.findByGroupId(groupId);
        log.info("[NOTICE][GET][DB] groupId={} - {}건 조회 완료", groupId, noticeList.size());

        // 3. 변환
        List<NoticeResponse> responseList = noticeList.stream()
                .map(n -> new NoticeResponse(n.getNoticeId(), n.getContent(), n.isPinned()))
                .toList();

        log.info("[NOTICE][GET][END] groupId={}, memberId={}, resultSize={} - 공지사항 조회 성공",
                groupId, memberId, responseList.size());

        return responseList;
    }

    /**
     * 지정된 그룹에서 해당 회원이 그룹장 권한을 가지고 있는지 검증한다.
     * <p>
     * 1. 멤버 ID와 그룹 ID로 그룹 멤버 정보를 조회
     * 2. 조회한 멤버의 역할(Role)이 그룹장인지 확인
     * </p>
     *
     * @param memberId 검증할 회원 ID
     * @param groupId  검증 대상 그룹 ID
     * @throws ServiceException 해당 멤버가 그룹에 존재하지 않는 경우
     *                           ({@link ErrorCode#GROUP_MEMBER_NOT_FOUND})
     * @throws DomainException  멤버가 그룹장이 아닌 경우
     */
    private void verifyAuthentification(Long memberId, Long groupId) {
        // 멤버 ID와 그룹 ID로 그룹 멤버 조회 (없으면 예외)
        GroupMember groupMember = groupMemberRepository
                .findGroupMemberByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new ServiceException(ErrorCode.GROUP_MEMBER_NOT_FOUND));

        // 그룹장 권한 검증 (도메인 객체 내 로직 사용)
        groupMember.verifyGroupLeader(groupMember.getRole());
    }
}
