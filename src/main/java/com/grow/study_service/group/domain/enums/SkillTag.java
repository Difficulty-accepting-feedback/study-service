package com.grow.study_service.group.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SkillTag: 기술/스킬 관련 태그 (카테고리별로 STUDY, HOBBY, MENTORING에 맞춤)
 * STUDY: 학습 중심, HOBBY: 취미 중심, MENTORING: 멘토링 중심으로 다양하게 추가
 */
@Getter
@AllArgsConstructor
public enum SkillTag {

    // STUDY 관련 (학습/지식 스킬)
    JAVA_PROGRAMMING("자바 프로그래밍"),
    PYTHON_DATA_SCIENCE("파이썬 데이터 사이언스"),
    ENGLISH_CONVERSATION("영어 회화"),
    MATH_PROBLEM_SOLVING("수학 문제 풀이"),
    HISTORY_EXPLORATION("역사 탐구"),
    FRENCH_LANGUAGE("프랑스어 학습"),
    ECONOMICS("경제"),
    AI_MACHINE_LEARNING("AI/머신러닝"),
    WEB_DEVELOPMENT("웹 개발"),
    DATABASE_MANAGEMENT("데이터베이스 관리"),
    TOEIC("토익 자격증"),

    // HOBBY 관련 (취미/여가 스킬)
    HIKING("등산"),
    COOKING("요리"),
    GUITAR_PLAYING("기타 연주"),
    PHOTOGRAPHY("사진 촬영"),
    BOOK_READING("독서"),
    GARDENING("원예"),
    BOARD_GAMES("보드게임"),
    PAINTING("회화"),
    YOGA("요가"),
    DANCING("댄스"),
    FISHING("낚시"),
    CYCLING("자전거 타기"),
    KNITTING("뜨개질"),
    TRAVEL_PLANNING("여행 계획"),
    MOVIE_APPRECIATION("영화 감상"),

    // MENTORING 관련 (전문/코칭 스킬)
    CAREER_COACHING("커리어 멘토링"),
    STARTUP_GUIDANCE("스타트업 창업 가이드"),
    LEADERSHIP_COACHING("리더십 코칭"),
    IT_JOB_PREPARATION("IT 취업 준비"),
    ART_MENTORING("예술가 멘토링"),
    FINANCIAL_INVESTMENT("금융 투자 조언"),
    HEALTH_COACHING("건강 관리 코칭"),
    PUBLIC_SPEAKING("공개 연설"),
    PROJECT_MANAGEMENT("프로젝트 관리"),
    MARKETING_STRATEGY("마케팅 전략"),
    NEGOTIATION_SKILLS("협상 기술"),
    TIME_MANAGEMENT("시간 관리"),
    CREATIVE_WRITING("창의적 글쓰기"),
    NETWORKING("네트워킹"),
    EMOTIONAL_INTELLIGENCE("감성 지능"),
    BACKEND_DEVELOPMENT("백엔드 개발"),  // 쿼리 예시 포함
    FRONTEND_DEVELOPMENT("프론트엔드 개발"),
    MOBILE_APP_DEVELOPMENT("모바일 앱 개발"),
    GRAPHIC_DESIGN("그래픽 디자인"),
    CONTENT_CREATION("콘텐츠 제작");

    private final String description;
}
