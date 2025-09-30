package com.grow.study_service.dashboard.application.impl;

import com.grow.study_service.dashboard.application.QuizService;
import com.grow.study_service.dashboard.presentation.dto.QuizRankDto;
import com.grow.study_service.group.application.api.MemberApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberApiService memberApiService;
    public static final String DAILY_QUIZ_RANK_KEY = "dailyQuizRank:";

    /**
     * Redis 에 저장된 퀴즈 점수를 가져와 QuizRankDto 로 변환하여 리턴합니다.
     * @param groupId 확인하고 싶은 그룹의 ID
     * @param memberId 확인할 멤버의 ID (현재 로직에는 직접 사용되지 않음 - 추후 해당 그룹 멤버가 맞는지 확인하는 로직이 필요함)
     * @return 퀴즈 점수를 담은 QuizRankDto 리스트 (순위 자동 부여)
     */
    @Override
    public List<QuizRankDto> getQuizRanking(Long groupId, Long memberId) {
        log.info("[DASHBOARD][QUIZ][START] groupId={}, memberId={}", groupId, memberId);
        String key = DAILY_QUIZ_RANK_KEY + groupId;

        Set<ZSetOperations.TypedTuple<String>> rankedScores = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, -1); // 내림차순 (높은 점수부터)

        if (rankedScores.isEmpty()) {
            return Collections.emptyList(); // 빈 리스트 반환 (퀴즈 권유 멘트용)
        }

        // memberId 리스트 추출 (String -> Long 변환)
        List<Long> memberIds = rankedScores.stream()
                .map(tuple -> Long.parseLong(tuple.getValue())) // memberId를 Long으로 변환
                .toList();

        List<String> nicknames = memberApiService.fetchMemberNames(memberIds); // API 호출 격리

        // 스트림으로 QuizRankDto 리스트 생성 (순위 자동 부여)
        return IntStream.range(0, memberIds.size())
                .mapToObj(i -> new QuizRankDto(
                        i + 1, // 순위 (1부터 시작)
                        nicknames.get(i),
                        rankedScores.stream()
                                .toList()
                                .get(i)
                                .getScore()
                                .intValue() // 점수 (Double -> int)
                ))
                .toList();
    }
}
