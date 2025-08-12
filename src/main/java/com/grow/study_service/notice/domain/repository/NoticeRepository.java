package com.grow.study_service.notice.domain.repository;

import com.grow.study_service.notice.domain.model.Notice;

public interface NoticeRepository {
    public void save(Notice notice);
}
