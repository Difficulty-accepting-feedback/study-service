package com.grow.study_service.group.application.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberApiServiceImpl implements MemberApiService {

    @Value("${member.name.path}")
    private String memberNamePath;

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
}