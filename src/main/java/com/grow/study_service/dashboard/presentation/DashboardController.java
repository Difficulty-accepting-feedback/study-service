package com.grow.study_service.dashboard.presentation;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.dashboard.application.DashboardService;
import com.grow.study_service.notice.application.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

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
}
