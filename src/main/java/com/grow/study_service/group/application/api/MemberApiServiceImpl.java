package com.grow.study_service.group.application.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberApiServiceImpl implements MemberApiService {

    @Value("${member.name.path}")
    private String memberNamePath;

    @Value("${member.info.path}")
    private String memberInfoPath;

    private final WebClient webClient;

    /**
     * 멤버 서비스에서 주어진 멤버 ID에 해당하는 멤버의 이름을 조회합니다.
     *
     * 이 메서드는 MSA 환경에서 WebClient를 사용하여 멤버 서비스에 동기 HTTP GET 요청을 보내
     * 멤버 이름을 실시간으로 가져옵니다. 동기 통신을 선택한 이유는 그룹 조회 시 즉시 필요한
     * 데이터이므로 이벤트 기반 비동기 통신보다 직접적인 데이터 조회가 적합하기 때문입니다.
     *
     * <p><strong>통신 방식:</strong></p>
     * <ul>
     *   <li><strong>프로토콜:</strong> HTTP GET</li>
     *   <li><strong>클라이언트:</strong> Spring WebClient (비동기 지원하지만 block()으로 동기화)</li>
     *   <li><strong>URI 패턴:</strong> memberNamePath에 memberId를 Path Variable로 전달</li>
     *   <li><strong>응답 형식:</strong> String (멤버 이름)</li>
     * </ul>
     *
     * <p><strong>에러 처리:</strong></p>
     * <ul>
     *   <li><strong>4xx Client Error:</strong> 잘못된 요청 데이터 (존재하지 않는 memberId 등)</li>
     *   <li><strong>5xx Server Error:</strong> 멤버 서비스 내부 오류</li>
     *   <li>모든 에러는 RuntimeException으로 래핑되어 호출자에게 전파됩니다</li>
     * </ul>
     *
     * <p><strong>성능 고려사항:</strong></p>
     * <ul>
     *   <li>block() 메서드 사용으로 인한 동기 처리로 스레드 블로킹 발생</li>
     *   <li>네트워크 지연시간에 따른 응답 시간 변동 가능</li>
     *   <li>멤버 서비스 가용성에 직접적으로 의존</li>
     * </ul>
     *
     * @param memberId 조회할 멤버의 고유 ID. null이 아니어야 하며, 양의 정수여야 합니다.
     * @return 해당 멤버의 이름 (String). 멤버가 존재하지 않을 경우 빈 문자열이 아닌 예외 발생.
     * @throws RuntimeException 멤버 서비스 호출 실패 시 발생 (4xx, 5xx 응답 코드 포함)
     * @see WebClient#get()
     * @see org.springframework.web.reactive.function.client.WebClient.ResponseSpec#onStatus
     */
    @Override
    public String getMemberName(Long memberId) {
        return webClient.get()
                .uri(memberNamePath, memberId) // 멤버 이름을 가져올 API 경로
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("API 호출 실패: " + response.statusCode())))
                .bodyToMono(String.class)
                .block();
    }

    /**
     * 주어진 멤버 ID 리스트에 해당하는 멤버 이름들을 조회합니다.
     *
     * 이 메서드는 각 멤버 ID에 대해 {@link #getMemberName(Long)} 메서드를 호출하여
     * 이름을 가져온 후, 리스트 형태로 반환합니다. MSA 환경에서 WebClient를 통해
     * 멤버 서비스에 동기 HTTP 요청을 보내 실시간으로 데이터를 조회합니다.
     *
     * @param memberIds 조회할 멤버 ID들의 리스트 (null 또는 빈 리스트일 경우 빈 리스트 반환)
     * @return 각 멤버 ID에 해당하는 이름들의 리스트 (순서 유지)
     */
    @Override
    public List<String> fetchMemberNames(List<Long> memberIds) {
        return memberIds.stream()
                .map(this::getMemberName)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 memberId 리스트를 기반으로 외부 서버에서 닉네임과 점수를 비동기 병렬 요청으로 조회합니다.
     * WebClient를 활용해 여러 요청을 동시에 처리하여 성능을 최적화합니다.
     * 에러 발생 시 로그를 남기고 빈 MemberInfo를 반환하며, 전체 결과를 Mono<List<MemberInfo>>로 제공합니다.
     *
     * @param memberIds 조회할 회원 ID 리스트 (필수, 빈 리스트 시 빈 결과 반환)
     * @return Mono<List<MemberInfo>> - 닉네임과 점수 리스트 (비동기 처리 결과)
     */
    @Override
    public Mono<List<MemberInfo>> getNicknameAndScore(List<Long> memberIds) {
        if (memberIds.isEmpty()) {
            return Mono.just(List.of()); // 입력 리스트가 비어 있으면, 불필요한 작업을 피하고 바로 빈 리스트를 Mono로 감싸 반환
        }

        // 전체 흐름: ID 리스트를 Flux로 변환 → 병렬로 API 호출 → 결과 모아서 리스트 반환
        return Flux.fromIterable(memberIds) // 리스트(memberIds)를 Flux(스트림)로 변환 Flux: "여러" 아이템을 순차적으로 처리
                .parallel() // Flux를 병렬 모드로 전환 → 여러 스레드에서 동시에 작업을 나눠 처리
                .runOn(Schedulers.parallel()) // 병렬 처리를 위한 스케줄러를 지정 Schedulers.parallel(): CPU 코어 수에 맞는 스레드 풀을 사용해 병렬 작업을 실행
                .flatMap(memberId -> webClient.get() // 입력을 받아 새로운 Mono/Flux를 반환하고, 이를 평평하게(flatten) 합침
                        .uri(memberInfoPath, memberId)
                        .retrieve() // API 응답을 처리하기 시작
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                response -> Mono.error(new RuntimeException("API 호출 실패: " + response.statusCode())))
                        .bodyToMono(MemberInfo.class) // 응답 본문을 MemberInfo 객체로 변환
                        .onErrorResume(e -> { // 에러가 발생해도 스트림이 중단되지 않도록 처리
                            log.error("[Member Info API error] API 호출 실패: {}", e.getMessage());
                            return Mono.empty(); // 에러 발생 시 빈 MemberInfo 반환
                        }))
                .sequential() // 병렬 처리 결과를 순차 Flux 로 합침 (병렬 처리 후 결과를 순서대로 모으는 데 필요)
                .collectList(); // 결과를 리스트로 반환
    }

    @Getter
    @AllArgsConstructor
    public static class MemberInfo {
        private String nickname;
        private double score;
    }
}