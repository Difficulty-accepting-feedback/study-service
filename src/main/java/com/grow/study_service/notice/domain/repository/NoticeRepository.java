package com.grow.study_service.notice.domain.repository;

import com.grow.study_service.notice.domain.model.Notice;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository {
    void save(Notice notice);
    void saveAll(List<Notice> notices);
    Optional<Notice> findByNoticeId(Long noticeId);
    List<Notice> findByGroupId(Long groupId);
    Optional<Notice> findByIsPinnedTrue(Long groupId);
    void deleteById(Long noticeId);
}
