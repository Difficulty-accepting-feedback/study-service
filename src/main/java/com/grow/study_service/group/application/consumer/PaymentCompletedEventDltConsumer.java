package com.grow.study_service.group.application.consumer;

import com.grow.study_service.common.slack.SlackErrorSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCompletedEventDltConsumer {

    private final SlackErrorSendService slackErrorSendService;

    @KafkaListener(
            topics = "payment-completed.dlt",
            groupId = "payment-dlt-service"
    )
    public void consumePaymentCompletedDlt(String message) {
        log.info("[PAYMENT COMPLETED DLT] 결제 완료 이벤트 실패 이벤트 수신: {}", message.trim());

        // TODO 로그 시스템에 전송 or 모니터링 카운트 증가

        // 슬랙으로 알림 전송
        slackErrorSendService.sendError("그룹 가입 - 알림 전송 실패",
                "카테고리: [PAYMENT COMPLETED ERROR]\n상세: 멘토링 결제 완료 이벤트 수신에 실패하였습니다.\n영향: 결제 완료 후 사용자가 그룹에 포함되지 않아, 본인의 그룹 내역을 조회했을 때, 해당 내역을 확인할 수 없습니다.",
                message);
    }
}
