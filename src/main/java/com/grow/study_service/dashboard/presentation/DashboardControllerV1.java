package com.grow.study_service.dashboard.presentation;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.dashboard.application.DashboardService;
import com.grow.study_service.kanbanboard.presentation.controller.KanbanBoardController;
import com.grow.study_service.kanbanboard.presentation.dto.response.KanbanBoardResponse;
import com.grow.study_service.notice.application.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/study/dashboard")
public class DashboardControllerV1 {

    private final NoticeService noticeService;
    private final DashboardService dashboardService;

    // 대시보드에 입장 -> 그룹의 출석률을 올려 줄 수 있도록 하는 API
    @PostMapping("/update-rate/{groupId}")
    public RsData<Void> incrementAttendanceDays(@RequestHeader("X-Authorization-Id") Long memberId,
                                                @PathVariable("groupId") Long groupId) {

        dashboardService.incrementAttendanceDays(groupId, memberId);

        return new RsData<>(
                "200",
                "출석일 업데이트 완료"
        );
    }

    // 대시보드에서 한줄 공지 보여주는 API
    @GetMapping("/notice-pinned/{groupId}")
    public RsData<String> getNoticePinned(@RequestHeader("X-Authorization-Id") Long memberId,
                                          @PathVariable("groupId") Long groupId) {

        String pinnedNotice = noticeService.getPinnedNotice(groupId);

        return new RsData<>(
                "200",
                "공지사항 조회 완료",
                pinnedNotice
        );
    }

    // 내 출석률 체크하여 보여 주는 API
    @GetMapping("/attendance-rate/{groupId}")
    public RsData<Double> getAttendanceRate(@RequestHeader("X-Authorization-Id") Long memberId,
                                            @PathVariable("groupId") Long groupId) {

        Double attendanceRate = dashboardService.getAttendanceRate(groupId, memberId);

        return new RsData<>(
                "200",
                "출석률 조회 완료",
                attendanceRate
        );
    }

    // 현재 진행률을 나타내는 퍼센트 API
    @GetMapping("/remaining-percentage/{groupId}")
    public RsData<Double> getProgressPercentage(@RequestHeader("X-Authorization-Id") Long memberId,
                                                @PathVariable("groupId") Long groupId) {

        Double progressPercentage = dashboardService.getProgressPercentage(groupId, memberId);

        return new RsData<>(
                "200",
                "진행률 퍼센트 조회 완료",
                progressPercentage
        );
    }

    // == 칸반보드 (To-do list) API ==

    /**
     * == 칸반보드 (To-do list) API ==
     * TodoController 에서 구현될 예정입니다.
     *
     * @see KanbanBoardController
     */

}
