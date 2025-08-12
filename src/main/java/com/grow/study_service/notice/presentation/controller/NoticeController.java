package com.grow.study_service.notice.presentation.controller;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.notice.application.service.NoticeService;
import com.grow.study_service.notice.presentation.dto.NoticeSaveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /** 공지사항 관련 기능을 처리하는 서비스 레이어 */
    private final NoticeService noticeService;

    /**
     * 신규 공지사항을 저장하는 API
     *
     * <p>요청 바디로 전달받은 {@link NoticeSaveRequest} 리스트를 서비스에 전달하여 저장한다.</p>
     *
     * @param request 저장할 공지사항 정보 목록
     *                (JSON 배열 형식, 각 원소는 {@link NoticeSaveRequest} 규격이어야 함)
     * @return 저장 처리 결과를 담은 {@link RsData} 객체
     *
     * @see NoticeSaveRequest
     * @see NoticeService#saveNotice(List)
     */
    @PostMapping("/save")
    public RsData<String> saveNotice(@Valid @RequestBody List<NoticeSaveRequest> request) {
        log.info("[Notice Save] 공지 사항 저장 시작");

        noticeService.saveNotice(request);

        log.info("[Notice Save] 공지 사항 저장 완료");

        return new RsData<>(
                "201",
                "공지사항 저장 완료",
                null
        );
    }

    // 공지사항 중 컨텐츠, pin 여부를 변경하는 API

    // 공지사항 삭제 API
}
