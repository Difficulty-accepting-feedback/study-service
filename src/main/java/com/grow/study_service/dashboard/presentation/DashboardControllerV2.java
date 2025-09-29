package com.grow.study_service.dashboard.presentation;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.dashboard.application.DashboardService;
import com.grow.study_service.dashboard.presentation.dto.DashboardResponseDto;
import com.grow.study_service.kanbanboard.application.KanbanBoardService;
import com.grow.study_service.kanbanboard.presentation.dto.response.KanbanBoardResponse;
import com.grow.study_service.notice.application.service.NoticeService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/study/dashboard")
public class DashboardControllerV2 {

    private final NoticeService noticeService;
    private final DashboardService dashboardService;
    private final KanbanBoardService kanbanBoardService;

    // 통합 대시보드 API
    @GetMapping("/{groupId}")
    @Timed(value = "dashboard.get")
    public ResponseEntity<RsData<DashboardResponseDto>> getDashboard(@RequestHeader("X-Authorization-Id") Long memberId,
                                                                     @PathVariable("groupId") Long groupId) {

        dashboardService.incrementAttendanceDays(groupId, memberId); // 출석률 증가
        String pinnedNotice = noticeService.getPinnedNotice(groupId); // 고정된 공지사항 조회
        Double attendanceRate = dashboardService.getAttendanceRate(groupId, memberId); // 출석률 조회
        Double progressPercentage = dashboardService.getProgressPercentage(groupId, memberId); // 진행률 조회
        List<KanbanBoardResponse> responses = kanbanBoardService.getTodos(memberId, groupId); // 칸반보드 조회
        // 클라이언트: 대시보드 페이지에 오늘의 퀴즈 버튼 하나 만들어 두기 -> 클릭하면 퀴즈 API 로 연동
        // TODO 오늘의 퀴즈 랭킹 도입

        RsData<DashboardResponseDto> response = new RsData<>(
                "200",
                "대시보드 조회 완료",
                new DashboardResponseDto(pinnedNotice, attendanceRate, progressPercentage, responses)
        );

        return ResponseEntity.ok(response);
    }
}