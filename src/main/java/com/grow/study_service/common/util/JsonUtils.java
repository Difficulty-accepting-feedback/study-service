package com.grow.study_service.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonUtils {

    private JsonUtils() {}

    /**
     * 주어진 객체를 JSON 문자열로 변환합니다.
     *
     * 이 메서드는 Jackson의 ObjectMapper를 사용하여 객체를 직렬화합니다.
     * 변환 중 오류가 발생하면 RuntimeException을 던집니다.
     *
     * @param object JSON으로 변환할 객체. null이 허용되며, null은 "null" 문자열로 변환됩니다.
     * @return 객체를 나타내는 JSON 문자열.
     * @throws RuntimeException JSON 직렬화에 실패할 경우 발생합니다. 내부적으로 JsonProcessingException을 래핑합니다.
     */
    public static String toJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

    /**
     * 주어진 JSON 문자열을 지정된 클래스 타입으로 역직렬화합니다.
     * 이 메서드는 Jackson의 ObjectMapper를 사용하여 JSON을 파싱하며,
     * 제네릭 타입 T를 통해 반환 타입을 동적으로 결정합니다.
     *
     * @param <T> 반환될 객체의 타입 (clazz에 의해 결정됨)
     * @param json 역직렬화할 JSON 문자열. null 또는 빈 문자열은 예외를 발생시킬 수 있음.
     * @param clazz JSON을 매핑할 대상 클래스 타입 (예: PaymentCompletedDto.class).
     * @return JSON 문자열이 역직렬화된 T 타입의 객체.
     * @throws RuntimeException JSON 파싱에 실패할 경우 발생 (내부적으로 JsonProcessingException을 래핑).
     */
    public static <T> T fromJsonString(String json, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }
}
