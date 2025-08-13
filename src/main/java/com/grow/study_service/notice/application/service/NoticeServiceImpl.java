package com.grow.study_service.notice.application.service;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import com.grow.study_service.notice.domain.model.Notice;
import com.grow.study_service.notice.domain.repository.NoticeRepository;
import com.grow.study_service.notice.presentation.dto.NoticeSaveRequest;
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
        log.info("[Notice Save] 공지사항 저장 시작 (1/2)");

        // 권한 검증을 위해 멤버 ID와 그룹 ID를 조합하여 조회
        GroupMember groupMember = groupMemberRepository
                .findGroupMemberByMemberIdAndGroupId(memberId, request.getGroupId())
                .orElseThrow(() -> new ServiceException(ErrorCode.GROUP_MEMBER_NOT_FOUND));

        // 권한 검증 로직 수행
        groupMember.verifyGroupLeader(groupMember.getRole());

        noticeRepository.save(
                Notice.create(
                        request.getGroupId(),
                        request.getContent(),
                        request.getIsPinned()
                ));
        log.info("[Notice Save] 공지사항 저장 완료 (2/2)");
    }

    /**
     * 신규 공지사항 목록 저장
     *
     * <p>전달받은 {@link NoticeSaveRequest} 리스트를
     * {@link Notice} 엔티티로 변환 후 DB에 저장한다.</p>
     *
     * <p><b>트랜잭션</b>:
     * 이 메서드는 {@link Transactional}로 보장되며, 저장 중 예외 발생 시 롤백된다.</p>
     *
     * @param request 저장할 공지사항 요청 DTO 목록
     */
    @Override
    @Transactional
    public void saveNotices(List<NoticeSaveRequest> request) {
        // 임시 저장용 공지사항 목록
        List<Notice> notices = new ArrayList<>();

        for (NoticeSaveRequest n : request) {
            log.info("[Notice Save] 공지사항 저장: 내용={}", n.getContent());
            notices.add(Notice.create(
                    n.getGroupId(),
                    n.getContent(),
                    n.getIsPinned()
            ));
        }

        noticeRepository.saveAll(notices);
    }
}
