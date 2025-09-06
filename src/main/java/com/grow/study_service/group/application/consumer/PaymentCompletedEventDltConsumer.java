package com.grow.study_service.group.application.consumer;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookPayloads;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;

// 결제 완료 이벤트가 실패한 경우에 대해 처리하는 Consumer
@Slf4j
@Service
public class PaymentCompletedEventDltConsumer {

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @KafkaListener(
            topics = "payment-completed.dlt",
            groupId = "payment-dlt-service"
    )
    public void consumePaymentCompletedDlt(String message) {
        log.info("[PAYMENT COMPLETED DLT] 결제 완료 이벤트 실패 이벤트 수신: {}", message.trim());

        // TODO 로그 시스템에 전송 or 모니터링 카운트 증가

        // 슬랙으로 알림 전송
        Slack slack = Slack.getInstance();

        // 현재 시간(KST) 동적 생성
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 에러 메시지 구성
        String errorDetails = "카테고리: [GROUP]\n상세: GROUP MEMBER 등록에 실패하였습니다.\n발생 시간: " + currentTime + "\n영향: 결제 완료 후 사용자가 그룹에 포함되지 않아, 그룹 확인 지연 가능";

        try {
            slack.send(slackWebhookUrl, WebhookPayloads.payload(p -> p.blocks(asBlocks(
                    header(h -> h.text(plainText("⚠️ 오류 알림: 멘토링 결제 완료 이벤트 수신 실패", true))),
                    section(s -> s.text(plainText(errorDetails)))
            ))));
        } catch (IOException e) {
            log.warn("[PAYMENT COMPLETED DLT] 결제 완료 이벤트 실패 이벤트 수신 실패: {}", e.getMessage());
            throw new RuntimeException("slack 에 오류 메시지 전송 실패", e); // 뭔 오류를 던져야 됨... 참나... 귀찮게
        }
    }
}
