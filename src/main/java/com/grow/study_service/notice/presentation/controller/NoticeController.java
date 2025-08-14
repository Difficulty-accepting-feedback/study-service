package com.grow.study_service.notice.presentation.controller;

import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.notice.application.service.NoticeService;
import com.grow.study_service.notice.presentation.dto.NoticeResponse;
import com.grow.study_service.notice.presentation.dto.NoticeSaveRequest;
import com.grow.study_service.notice.presentation.dto.NoticeUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공지사항 관련 REST 컨트롤러
 *
 * <p>공지사항 생성, 수정, 삭제 등 API 엔드포인트를 제공한다.</p>
 * <p>본 클래스는 REST API 방식으로 동작하며,
 * JSON 형식의 요청을 RequestBody로 받아 비즈니스 로직(Service)으로 전달한다.</p>
 *
 * @author sun
 * @since 1.0 (2025-08-13)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

    /**
     * 공지사항 관련 기능을 처리하는 서비스 레이어
     */
    private final NoticeService noticeService;

    /**
     * 신규 공지사항 저장 API.
     * <p>요청자의 회원 ID와 저장할 공지사항 정보를 받아 신규 공지를 등록한다.</p>
     *
     * @param memberId RequestHeader("X-Authorization-Id") - 요청 회원 ID
     * @param request  RequestBody(JSON) - 저장할 공지사항 데이터
     *
     * @return 처리 결과 메시지
     * @throws DomainException 권한이 없거나 요청 데이터가 유효하지 않은 경우
     */
    @PostMapping("/save")
    public RsData<String> saveNotice(@RequestHeader("X-Authorization-Id") Long memberId,
                                     @Valid @RequestBody NoticeSaveRequest request) {
        noticeService.saveNotice(memberId, request);
        return new RsData<>("201",
                "공지사항 저장 완료",
                null
        );
    }

    /**
     * 지정된 그룹의 공지사항을 전체 업데이트한다.
     * <p>내용 수정, 메인 고정 여부 변경 등을 포함하여 여러 건을 한 번에 처리</p>
     *
     * @param groupId   PathVariable - 그룹 ID
     * @param memberId  RequestHeader("X-Authorization-Id") - 요청한 회원 ID
     * @param request   RequestBody(JSON) - 수정할 공지사항 목록
     *
     * @return 처리 결과 메시지
     *
     * @throws DomainException 권한이 없거나 요청 데이터가 유효하지 않을 경우
     */
    @PutMapping("/update/{groupId}")
    public RsData<String> updateNotice(@PathVariable("groupId") Long groupId,
                                       @RequestHeader("X-Authorization-Id") Long memberId,
                                       @Valid @RequestBody List<NoticeUpdateRequest> request) {
        noticeService.updateNotices(groupId, memberId, request);
        return new RsData<>("200",
                "공지사항 업데이트 완료",
                null
        );
    }

    /**
     * 특정 그룹의 모든 공지사항을 조회하는 API.
     * <p>회원이 해당 그룹에 속해 있는지 검증 후, 공지사항 목록을 반환한다.</p>
     *
     * @param groupId  PathVariable - 조회할 그룹 ID
     * @param memberId RequestHeader("X-Authorization-Id") - 요청 회원 ID
     * @return 공지사항 목록과 처리 결과를 담은 {@link RsData}
     *
     * @throws DomainException 요청자가 그룹에 속하지 않았거나 접근 권한이 없는 경우
     */
    @GetMapping("/{groupId}")
    public RsData<List<NoticeResponse>> getNotices(@PathVariable("groupId") Long groupId,
                                                   @RequestHeader("X-Authorization-Id") Long memberId) {

        List<NoticeResponse> notices = noticeService.getNotices(groupId, memberId);

        return new RsData<>("200",
                "공지사항 조회 완료",
                notices
        );
    }

    @DeleteMapping("/{groupId}/{noticeId}")
    public RsData<String> deleteNotice(@PathVariable("groupId") Long groupId,
                                       @PathVariable("noticeId") Long noticeId,
                                       @RequestHeader("X-Authorization-Id") Long memberId) {
        noticeService.deleteNotice(groupId, noticeId, memberId);
        return new RsData<>("200",
                "공지사항 삭제 완료",
                null
        );
    }
}
