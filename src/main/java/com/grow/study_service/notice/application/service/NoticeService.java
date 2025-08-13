package com.grow.study_service.notice.application.service;

import com.grow.study_service.notice.presentation.dto.NoticeSaveRequest;

import java.util.List;

public interface NoticeService {
    void saveNotice(Long memberId, NoticeSaveRequest request);
    void saveNotices(List<NoticeSaveRequest> request);
}
