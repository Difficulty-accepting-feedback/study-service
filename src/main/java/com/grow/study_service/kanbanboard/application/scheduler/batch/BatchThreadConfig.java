package com.grow.study_service.kanbanboard.application.scheduler.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class BatchThreadConfig {

    /**
     * 배치 작업에서 병렬 처리를 위한 TaskExecutor 빈을 생성합니다.
     * 왜 필요한가?
     * - 대량 데이터를 처리할 때 단일 스레드로 순차 실행하면 시간이 오래 걸리고 CPU 자원을 효율적으로 사용하지 못합니다.
     * - TaskExecutor를 사용하면 여러 스레드로 작업을 분산하여 병렬 처리할 수 있어, 전체 실행 시간을 단축합니다.
     */
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // 최소 스레드 수 (기본 4) - 항상 유지되는 스레드 수로, 작업이 없어도 대기
        executor.setMaxPoolSize(10); // 최대 스레드 수
        executor.setQueueCapacity(25); // 대기 큐 크기 (초과된 작업이 큐에 쌓임) - 스레드가 모두 사용 중일 때 대기
        executor.setThreadNamePrefix("kanban-batch-"); // 스레드 이름 prefix - 로그에서 스레드를 쉽게 식별
        executor.setWaitForTasksToCompleteOnShutdown(true); // 스레드 종료 후 작업 종료 대기
        executor.setAwaitTerminationSeconds(60); // 종료 대기 시간 60초 - 강제 종료 전 대기 시간
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 큐가 가득 찼을 때 정책 (CallerRunsPolicy: 호출자 스레드에서 직접 처리) - 과부하 방지
        executor.initialize();
        return executor;  // 스케줄러에 등록
    }
}
