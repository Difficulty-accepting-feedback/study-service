package com.grow.study_service.notice.presentation.controller;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.notice.application.service.NoticeService;
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

    @PostMapping("/save")
    public RsData<String> saveNotice(@RequestHeader("X-Authorization-Id") Long memberId,
                                     @Valid @RequestBody NoticeSaveRequest request) {
        noticeService.saveNotice(memberId, request);
        return new RsData<>("201",
                "공지사항 저장 완료",
                null
        );
    }

    // 공지사항 업데이트 API (내용 변경 등)
    @PutMapping("/update/{groupId}") // 전체 수정 가능하도록 함
    public RsData<String> updateNotice(@PathVariable("groupId") Long groupId,
                                       @RequestHeader("X-Authorization-Id") Long memberId,
                                       @Valid @RequestBody List<NoticeUpdateRequest> request) {
        noticeService.updateNotices(groupId, memberId, request);
        return new RsData<>("200",
                "공지사항 업데이트 완료",
                null
        );
    }

    // 공지사항 조회 API

    // 공지사항 삭제 API
}
