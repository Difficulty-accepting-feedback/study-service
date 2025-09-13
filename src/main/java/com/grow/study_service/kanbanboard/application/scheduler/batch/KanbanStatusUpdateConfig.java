package com.grow.study_service.kanbanboard.application.scheduler.batch;

import com.grow.study_service.kanbanboard.domain.model.KanbanBoard;
import com.grow.study_service.kanbanboard.infra.persistence.entity.KanbanBoardJpaEntity;
import com.grow.study_service.kanbanboard.infra.persistence.mapper.KanbanBoardMapper;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

/**
 * 이전 로직의 문제점
 * 1. 단건 업데이트 -> 데이터가 많아질수록 save 를 매번 호출할 시에 I/O 부하가 커질 것이다
 * 2. 단일 스레드 사용 -> 병렬 처리가 되지 않음
 * 3. 재시도에 관한 처리가 부족함
 *
 * 스프링 배치 프로세스 추가 후
 * 1. 청크(100) 단위로 읽기/처리/쓰기 -> 메모리 절약, DB 호출 감소
 * 2. taskExecutor 를 step 에 추가해 병렬 처리 -> 멀티 스레딩 활용
 * 3. Spring Batch는 실패 시 재시도/스킵 기능 제공 -> 재시도에 관해 스케줄링을 구현할 필요 없음
 */

/**
 * KanbanBoard 상태 업데이트를 위한 Spring Batch 설정 클래스입니다.
 * 이 클래스는 매일 자정에 실행되는 배치 작업을 정의하며, Spring Batch를 활용해 대량 데이터 처리를 효율적으로 수행합니다.
 * 데이터 조회(Reader), 처리(Processor), 저장(Writer)을 청크 단위로 나누어 병렬 처리하는 구조입니다.
 * 이는 기존 루프 기반 처리보다 메모리 효율적이고, 실패 시 재시도/재개가 가능하며, 성능 최적화에 유리합니다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class KanbanStatusUpdateConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final TaskExecutor taskExecutor;
    private final JobLauncher jobLauncher;

    /**
     * KanbanBoard 상태 업데이트를 위한 Spring Batch Job 빈을 생성합니다.
     *
     * Job은 배치 작업의 최상위 단위로, 하나 이상의 Step을 순서대로 실행합니다.
     * Job은 실행 이력 관리, 실패 시 재시작, 파라미터 기반 중복 실행 방지 등의 기능을 제공합니다.
     * 여기서는 하나의 Step만 포함되지만, 나중에 복잡한 작업(예: 여러 Step)으로 확장 가능합니다.
     * JobRepository를 통해 Job 실행 상태를 DB에 저장/관리하여 안정성을 높입니다.
     */
    @Bean
    public Job updateKanbanStatusJob() {
        Job updateKanbanStatusJob = new JobBuilder("updateKanbanStatusJob", jobRepository)
                .start(updateKanbanStatusStep())
                .build();

        log.info("JOB 빈 생성 완료: updateKanbanStatusJob");

        return updateKanbanStatusJob;
    }

    /**
     * Job 내에서 실제 작업을 수행하는 Step 빈을 생성합니다.
     *
     * - Step은 Job의 세부 단계로, 청크 기반(chunk-oriented) 처리 모델을 사용합니다.
     * - chunk(100): 100개 단위로 데이터를 읽고 처리 후 저장 - 메모리 효율적, 트랜잭션 분리 (실패 시 해당 청크만 롤백).
     * - reader: DB에서 데이터를 읽음.
     * - processor: 각 항목을 처리 (상태 업데이트).
     * - writer: 처리된 항목을 DB에 저장.
     * - taskExecutor: 병렬 처리 활성화 - 여러 스레드로 청크를 동시에 처리해 속도 향상.
     * - transactionManager: 각 청크의 트랜잭션 관리 - ACID 보장.
     *
     * 이 구조는 대량 데이터에서 루프 기반 처리보다 훨씬 효율적.
     */
    @Bean
    public Step updateKanbanStatusStep() {
        return new StepBuilder("updateKanbanStatusStep", jobRepository)
                // <읽는 타입 , 쓰는 타입> ― 둘 다 KanbanBoardJpaEntity
                .<KanbanBoardJpaEntity, KanbanBoardJpaEntity>chunk(100, transactionManager)
                .reader(kanbanBoardReader())
                .processor(kanbanBoardProcessor())
                .writer(kanbanBoardWriter())
                .taskExecutor(taskExecutor)
                .build();
    }

    /**
     * DB에서 KanbanBoard 데이터를 페이징 방식으로 읽어오는 Reader 빈을 생성합니다.
     * (대량 데이터를 한 번에 메모리로 로드하면 OutOfMemoryError가 발생할 수 있기 때문)
     *
     * JpaPagingItemReader는 페이징(pageSize=100)으로 데이터를 청크 단위로 읽어 메모리 부하를 줄입니다.
     * - queryString: JPQL 쿼리로, startDate가 현재 시간(now)과 일치하는 데이터만 필터링.
     * - parameterValues: 동적 파라미터(now)를 설정 - 매 Job 실행 시 최신 시간 적용.
     * - entityManagerFactory: JPA 연결을 위해 필요 - 엔티티 매니저를 통해 DB 접근.
     * 이 Reader는 배치의 입력 소스로, 효율적인 데이터 로딩을 보장합니다.
     */
    @Bean
    public JpaPagingItemReader<KanbanBoardJpaEntity> kanbanBoardReader() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.with(LocalTime.MIN);  // 해당 날짜의 00:00:00
        LocalDateTime endOfDay = now.with(LocalTime.MAX).plusSeconds(1);  // 다음 날 00:00:00 (포함되지 않음)

        return new JpaPagingItemReaderBuilder<KanbanBoardJpaEntity>()
                .name("kanbanBoardReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)  // 페이징으로 100개씩 읽기
                .queryString("SELECT k FROM KanbanBoardJpaEntity k " +
                        "WHERE k.startDate >= :startOfDay AND k.startDate < :endOfDay")  // 범위 비교로 날짜만 일치
                .parameterValues(Map.of("startOfDay", startOfDay, "endOfDay", endOfDay))
                .build();
    }

    /**
     * 읽어온 KanbanBoard를 처리하는 Processor 빈을 생성합니다.
     *
     * Reader에서 가져온 데이터를 비즈니스 로직으로 변환/수정해야 합니다.
     * ItemProcessor는 각 항목에 대해 updateStatus()를 호출해 상태를 변경합니다.
     * - 람다 표현으로 간단히 구현
     * - 각 KanbanBoard 객체를 받아 수정 후 반환.
     * - 병렬 처리 시 thread-safe 해야 함 (여기서는 도메인 메서드만 호출하므로 안전).
     *
     * Processor는 배치의 핵심 로직을 담당합니다.
     */
    @Bean
    public ItemProcessor<KanbanBoardJpaEntity, KanbanBoardJpaEntity> kanbanBoardProcessor() {
        return entity -> {
            KanbanBoard domain = KanbanBoardMapper.toDomain(entity); // 엔티티 → 도메인
            domain.updateStatus();                                   // 비즈니스 로직
            return KanbanBoardMapper.toEntity(domain);              // 도메인 → 엔티티
        };
    }


    /**
     * 처리된 KanbanBoard를 DB에 저장하는 Writer 빈을 생성합니다.
     *
     * - Processor 에서 수정된 데이터를 일괄 저장해야 합니다.
     * JpaItemWriter는 청크 단위(100개)로 엔티티를 DB에 flush 하여 배치 업데이트를 수행합니다.
     * - entityManagerFactory: JPA를 통해 엔티티를 영속화.
     * - application.properties의 batch_size와 함께 사용하면 단건 save 보다 훨씬 빠름.
     *
     * Writer는 배치의 출력 소스로, 트랜잭션 내에서 안전하게 저장합니다.
     */
    @Bean
    public JpaItemWriter<KanbanBoardJpaEntity> kanbanBoardWriter() {
        JpaItemWriter<KanbanBoardJpaEntity> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Scheduled(cron = "${kanban.status.update}")
    public void runUpdateKanbanStatusJob() throws Exception {
        log.info("[KANBAN][STATUS][START] KanbanBoard 상태 업데이트 Job 시작");

        JobParameters params = new JobParametersBuilder()
                .addString("run.id", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(updateKanbanStatusJob(), params);

        log.info("[KANBAN][STATUS][END] KanbanBoard 상태 업데이트 Job 완료");
    }
}
