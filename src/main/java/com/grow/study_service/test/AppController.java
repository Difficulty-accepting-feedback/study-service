package com.grow.study_service.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AppController {

    @GetMapping
    public String test() {
        return "스터디 서버 테스트 연결 - argo CD 자동 업데이트 성공";
    }
}
