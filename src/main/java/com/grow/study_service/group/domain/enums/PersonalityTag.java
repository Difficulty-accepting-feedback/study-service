package com.grow.study_service.group.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PersonalityTag: 성격/특성 관련 태그 (그룹의 분위기나 멤버 성향을 나타냄)
 */
@Getter
@AllArgsConstructor
public enum PersonalityTag {

    DILIGENT("성실한"),          // 꾸준하고 책임감 있음
    ACTIVE("활발한"),            // 에너지 넘치고 활동적
    CREATIVE("창의적인"),        // 아이디어 풍부하고 혁신적
    COLLABORATIVE("협력적인"),   // 팀워크를 중시함
    PASSIONATE("열정적인"),      // 열의가 넘침
    METICULOUS("꼼꼼한"),        // 세밀하고 정확함
    HUMOROUS("유머러스한"),      // 재미있고 밝음
    PROGRESSIVE("진취적인"),     // 앞으로 나아가는 성향
    PATIENT("인내심 있는"),     // 참을성 있음
    OPTIMISTIC("낙관적인"),      // 긍정적 사고
    ADVENTUROUS("모험적인"),     // 도전적이고 탐험 좋아함
    EMPATHETIC("공감하는"),      // 타인 감정을 잘 이해함
    DISCIPLINED("규율 있는"),   // 자기 관리 능력 강함
    INNOVATIVE("혁신적인"),      // 새로운 아이디어 창출
    RESILIENT("회복력 있는"),   // 어려움에서 잘 회복함
    CHARISMATIC("카리스마 있는"), // 매력적이고 리더십 있음
    MODEST("겸손한"),            // 자신을 과시하지 않음
    ANALYTICAL("분석적인"),      // 논리적 사고 강함
    ENTHUSIASTIC("열광적인"),    // 모든 일에 열정적
    SUPPORTIVE("지지하는"),      // 타인을 돕는 성향
    VERSATILE("다재다능한"),     // 여러 분야에 능함
    CALM("침착한"),              // 위기 시 안정적
    INSPIRING("영감을 주는"),   // 타인에게 동기부여
    LOYAL("충성스러운"),         // 관계를 소중히 함
    VIBRANT("생기 넘치는");      // 활기차고 에너지 충만

    private final String description;
}