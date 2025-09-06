package com.grow.study_service.group.application.consumer;

import com.grow.study_service.common.util.JsonUtils;
import com.grow.study_service.group.application.dto.PaymentCompletedDto;
import com.grow.study_service.groupmember.domain.enums.Role;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCompletedEventConsumer {

    private final GroupMemberRepository groupMemberRepository;

    @KafkaListener(
            topics = "payment-completed",
            groupId = "payment-service",
            concurrency = "3"
    )
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 1000, multiplier = 2),
            dltTopicSuffix = ".dlt"
    )
    @Transactional
    public void consumePaymentCompleted(String message) {
        log.info("[PAYMENT COMPLETED] 결제 완료 이벤트 수신: {}", message.trim());

        PaymentCompletedDto response = JsonUtils.fromJsonString(message, PaymentCompletedDto.class);

        GroupMember groupMember = GroupMember.create(
                response.getMemberId(),
                response.getGroupId(),
                Role.MEMBER
        );

        groupMemberRepository.save(groupMember);

        log.info("[PAYMENT COMPLETED] 결제 완료 이벤트 처리 완료: {}", JsonUtils.toJsonString(response));
    }
}