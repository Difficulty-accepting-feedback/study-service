package com.grow.study_service.test.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class TrafficController {

    // CPU 부하를 주는 API
    @GetMapping("/cpu")
    public String cpu() {
        log.info("CPU 부하를 주는 API 호출");
        // CPU 사용량 증가 -> loop 도는 게 직빵이라네요...
        long value = 0;
        for (int i = 0; i < 1000000000000L; i++) {
            value++;
        }
        return "TEST 실행 완료: value = " + value;
    }

    private List<String> list = new ArrayList<>();

    // JVM 부하를 주는 API
    @GetMapping("/jvm")
    public String jvm() {
        log.info("JVM 부하를 주는 API 호출");
        for (int i = 0; i < 1000000; i++) {
            list.add("jvm test " + i);
        }
        return "ok";
    }

    @Autowired
    DataSource dataSource;

    @GetMapping("/jdbc")
    public String jdbc() throws SQLException {
        log.info("JDBC 부하를 주는 API 호출");
        Connection connection = dataSource.getConnection();
        log.info("Connection: {}", connection);
        // connection.close(); -> 일부러 커넥션 풀 안 닫음 -> 10개 이상 쓰면 풀을 못 가져올 것임
        return "ok";
    }

    @GetMapping("/error-log")
    public String errorLog() {
        log.info("ERROR 로그를 출력하는 API 호출");
        return "ok";
    }

    @GetMapping("/error")
    public String error() {
        log.error("ERROR를 발생시키는 API 호출");
        return "error";
    }
}
