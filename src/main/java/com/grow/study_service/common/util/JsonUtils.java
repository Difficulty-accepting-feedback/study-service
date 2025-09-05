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
}
