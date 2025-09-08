package com.grow.study_service.notice.application.service;

import com.grow.study_service.notice.presentation.dto.NoticeResponse;
import com.grow.study_service.notice.presentation.dto.NoticeSaveRequest;
import com.grow.study_service.notice.presentation.dto.NoticeUpdateRequest;

import java.util.List;

public interface NoticeService {
    void saveNotice(Long memberId, NoticeSaveRequest request);
    void updateNotices(Long groupId, Long memberId, List<NoticeUpdateRequest> request);
    List<NoticeResponse> getNotices(Long groupId, Long memberId);
    void deleteNotice(Long groupId, Long noticeId, Long memberId);
    String getPinnedNotice(Long groupId);
}
