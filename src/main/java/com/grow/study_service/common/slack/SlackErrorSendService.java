package com.grow.study_service.common.slack;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookPayloads;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;

@Slf4j
@Service
public class SlackErrorSendService {

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    public void sendError(String title, String errorDetails, String message) {
        // 슬랙으로 알림 전송
        Slack slack = Slack.getInstance();

        // 현재 시간(KST) 동적 생성
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try {
            slack.send(slackWebhookUrl, WebhookPayloads.payload(p -> p.blocks(asBlocks(
                    header(h -> h.text(plainText("⚠️ 오류 알림: " + title, true))),
                    section(s -> s.text(plainText(errorDetails))),
                    section(s -> s.text(plainText("발생 시간: " + currentTime))),
                    section(s -> s.text(plainText("메시지 미리 보기: " + message)))
            ))));
        } catch (IOException e) {
            log.warn("[DLT ERROR] 이벤트 처리 실패: {}", e.getMessage());
            throw new RuntimeException("slack 에 오류 메시지 전송 실패", e); // 뭔 오류를 던져야 됨... 참나... 귀찮게
        }
    }
}
