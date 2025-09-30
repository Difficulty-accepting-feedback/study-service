package com.grow.study_service.dashboard.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rank-test")
public class RankTestController {

    public static final String DAILY_QUIZ_RANK_KEY = "dailyQuizRank:";
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/clear")
    public String clear() {
        redisTemplate.delete(DAILY_QUIZ_RANK_KEY + "1");
        redisTemplate.delete(DAILY_QUIZ_RANK_KEY + 2);
        return "초기화 완료";
    }

    /**
     * 퀴즈 점수 저장 API (테스트 용도)
     */
    @PostMapping()
    public String saveAndView(@RequestParam("groupId") Long groupId,
                       @RequestParam("memberId") Long memberId,
                       @RequestParam("correct") Long correct) {

        String key = DAILY_QUIZ_RANK_KEY + groupId;
        redisTemplate.opsForZSet().add(key, memberId.toString(), correct);

        LocalDateTime expirationTime = LocalDate.now().plusDays(1).atStartOfDay();  // 다음 날 00:00
        redisTemplate.expireAt(key, Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()));

        Set<ZSetOperations.TypedTuple<String>> rankedScores = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, -1);  // 내림차순 (높은 점수부터)

        rankedScores
                .forEach(tuple ->
                        log.info("Member: {}, Score: {}", tuple.getValue(), tuple.getScore()));

        return "저장 완료";
    }
}
