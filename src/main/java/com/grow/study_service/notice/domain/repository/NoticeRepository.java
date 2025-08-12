package com.grow.study_service.notice.domain.repository;

import com.grow.study_service.notice.domain.model.Notice;

import java.util.List;

public interface NoticeRepository {
    public void save(Notice notice);
    public void saveAll(List<Notice> notices);
}
