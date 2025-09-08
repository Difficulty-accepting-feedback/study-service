package com.grow.study_service.dashboard.application;

public interface DashboardService {
    void incrementAttendanceDays(Long groupId, Long memberId);
    Double getAttendanceRate(Long groupId, Long memberId);
}