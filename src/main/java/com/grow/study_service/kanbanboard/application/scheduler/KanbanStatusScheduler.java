package com.grow.study_service.kanbanboard.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KanbanStatusScheduler {

    /*@Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void updateKanbanStatus() {
        List<KanbanBoard> findAll = kanbanBoardRepository.findAllByStartDateEquals(LocalDateTime.now());

        log.info("[KANBAN][STATUS][START] 총 {} 개의 KanbanBoard를 업데이트", findAll.size());
        ArrayList<KanbanBoard> list = new ArrayList<>();

        for (KanbanBoard kanbanBoard : findAll) {
            kanbanBoard.updateStatus();
            list.add(kanbanBoard);
        }

        list.forEach(kanbanBoardRepository::save);

        log.info("[KANBAN][STATUS][END] 총 {} 개의 KanbanBoard 업데이트 완료", list.size());
    }*/
}