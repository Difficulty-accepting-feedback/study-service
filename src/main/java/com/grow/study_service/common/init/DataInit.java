package com.grow.study_service.common.init;

import com.grow.study_service.board.domain.enums.BoardType;
import com.grow.study_service.board.domain.model.Board;
import com.grow.study_service.board.domain.repository.BoardRepository;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.enums.PersonalityTag;
import com.grow.study_service.group.domain.enums.SkillTag;
import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.group.domain.repository.GroupRepository;
import com.grow.study_service.groupmember.domain.enums.Role;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class DataInit implements CommandLineRunner {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public void run(String... args) throws Exception {
        // redis 초기화 로직 실행
        redisConnectionFactory.getConnection().serverCommands().flushAll();
        log.debug("redis flush all.");

        List<Group> groups = new ArrayList<>();

        // ------- 그룹 생성 ------- //

        // STUDY 카테고리: 7개
        groups.add(Group.create("자바 프로그래밍 스터디", Category.STUDY, "자바 초보자들을 위한 실습 중심 스터디 그룹입니다.", PersonalityTag.DILIGENT, SkillTag.JAVA_PROGRAMMING, 0));
        groups.add(Group.create("영어 회화 모임", Category.STUDY, "매일 영어 대화를 연습하며 실력을 키우는 학습 커뮤니티.", PersonalityTag.ACTIVE, SkillTag.ENGLISH_CONVERSATION, 0));
        groups.add(Group.create("데이터 사이언스 학습단", Category.STUDY, "파이썬과 머신러닝을 함께 공부하는 열정적인 그룹.", PersonalityTag.CREATIVE, SkillTag.PYTHON_DATA_SCIENCE, 0));
        groups.add(Group.create("역사 탐구 클럽", Category.STUDY, "고대 역사부터 현대사까지 깊이 있게 탐구합니다.", PersonalityTag.METICULOUS, SkillTag.HISTORY_EXPLORATION, 0));
        // groups.add(Group.create("수학 문제 풀이 그룹", Category.STUDY, "고등 수학 문제를 함께 풀며 논리력을 강화하세요.", PersonalityTag.ANALYTICAL, SkillTag.MATH_PROBLEM_SOLVING, 0));
        // groups.add(Group.create("프랑스어 배우기", Category.STUDY, "기초부터 고급까지 프랑스어를 재미있게 배우는 모임.", PersonalityTag.PATIENT, SkillTag.FRENCH_LANGUAGE, 0));
        // groups.add(Group.create("경제학 세미나", Category.STUDY, "경제 이론과 실생활 사례를 논의하는 지적 토론 그룹.", PersonalityTag.COLLABORATIVE, SkillTag.ECONOMICS, 0));

        // HOBBY 카테고리: 7개
        groups.add(Group.create("등산 동호회", Category.HOBBY, "주말마다 산을 오르며 자연을 즐기는 취미 모임.", PersonalityTag.ADVENTUROUS, SkillTag.HIKING, 0));
        groups.add(Group.create("요리 마스터 클럽", Category.HOBBY, "다양한 요리 레시피를 공유하고 함께 만드는 재미있는 그룹.", PersonalityTag.CREATIVE, SkillTag.COOKING, 0));
        groups.add(Group.create("기타 연주 모임", Category.HOBBY, "초보자부터 프로까지 기타를 치며 음악을 즐깁니다.", PersonalityTag.PASSIONATE, SkillTag.GUITAR_PLAYING, 0));
        groups.add(Group.create("사진 촬영 여행단", Category.HOBBY, "카메라를 들고 여행하며 아름다운 순간을 담아요.", PersonalityTag.VIBRANT, SkillTag.PHOTOGRAPHY, 0));
        groups.add(Group.create("독서 모임", Category.HOBBY, "매월 한 권의 책을 읽고 토론하는 문화 취미 그룹.", PersonalityTag.EMPATHETIC, SkillTag.BOOK_READING, 0));
        groups.add(Group.create("원예 가드닝 클럽", Category.HOBBY, "집에서 식물을 키우며 여유로운 시간을 보내는 모임.", PersonalityTag.CALM, SkillTag.GARDENING, 0));
        groups.add(Group.create("보드게임 파티", Category.HOBBY, "다양한 보드게임을 즐기며 친구를 사귀는 재미난 그룹.", PersonalityTag.HUMOROUS, SkillTag.BOARD_GAMES, 0));

        // MENTORING 카테고리: 7개
        groups.add(Group.create("커리어 멘토링 그룹", Category.MENTORING, "경력 개발을 위한 선배들의 조언을 공유하는 멘토링 모임.", PersonalityTag.SUPPORTIVE, SkillTag.CAREER_COACHING, 10000));
        groups.add(Group.create("스타트업 창업 가이드", Category.MENTORING, "창업 아이디어를 실현하기 위한 실전 멘토링 세션.", PersonalityTag.INNOVATIVE, SkillTag.STARTUP_GUIDANCE, 20000));
        groups.add(Group.create("리더십 코칭 클럽", Category.MENTORING, "리더십 스킬을 키우는 개인화된 멘토링 프로그램.", PersonalityTag.CHARISMATIC, SkillTag.LEADERSHIP_COACHING, 80000));
        groups.add(Group.create("IT 취업 준비단", Category.MENTORING, "IT 분야 취업을 위한 이력서 작성과 인터뷰 팁 공유.", PersonalityTag.DISCIPLINED, SkillTag.IT_JOB_PREPARATION, 120000));
        groups.add(Group.create("예술가 멘토링 네트워크", Category.MENTORING, "예술 창작자들을 위한 영감과 피드백 멘토링.", PersonalityTag.INSPIRING, SkillTag.ART_MENTORING, 30000));
        groups.add(Group.create("금융 투자 조언 모임", Category.MENTORING, "투자 초보자를 위한 전문가 멘토링 그룹.", PersonalityTag.ANALYTICAL, SkillTag.FINANCIAL_INVESTMENT, 40000));
        groups.add(Group.create("건강 관리 코칭", Category.MENTORING, "건강한 생활 습관을 위한 개인 멘토링과 조언.", PersonalityTag.RESILIENT, SkillTag.HEALTH_COACHING, 50000));

        groups.forEach(groupRepository::save);

        // ------- 그룹 멤버 생성 ------- //

        // 각 그룹별로 리더 1명, 멤버 1명씩 생성 (총 21개 그룹 × 2명 = 42명)
        List<GroupMember> groupMembers = new ArrayList<>();

        // 스터디 그룹 (1~7번)
        groupMembers.add(GroupMember.create(1L, 1L, Role.LEADER));   // 자바 프로그래밍 스터디 리더
        groupMembers.add(GroupMember.create(2L, 1L, Role.MEMBER));   // 자바 프로그래밍 스터디 멤버
        groupMembers.add(GroupMember.create(3L, 2L, Role.LEADER));   // 영어 회화 모임 리더
        groupMembers.add(GroupMember.create(4L, 2L, Role.MEMBER));   // 영어 회화 모임 멤버
        groupMembers.add(GroupMember.create(5L, 3L, Role.LEADER));   // 데이터 사이언스 학습단 리더
        groupMembers.add(GroupMember.create(6L, 3L, Role.MEMBER));   // 데이터 사이언스 학습단 멤버
        groupMembers.add(GroupMember.create(7L, 4L, Role.LEADER));   // 역사 탐구 클럽 리더
        groupMembers.add(GroupMember.create(8L, 4L, Role.MEMBER));   // 역사 탐구 클럽 멤버
        // groupMembers.add(GroupMember.create(9L, 5L, Role.LEADER));   // 수학 문제 풀이 그룹 리더
        // groupMembers.add(GroupMember.create(10L, 5L, Role.MEMBER));  // 수학 문제 풀이 그룹 멤버
        // groupMembers.add(GroupMember.create(11L, 6L, Role.LEADER));  // 프랑스어 배우기 리더
        // groupMembers.add(GroupMember.create(12L, 6L, Role.MEMBER));  // 프랑스어 배우기 멤버
        // groupMembers.add(GroupMember.create(13L, 7L, Role.LEADER));  // 경제학 세미나 리더
        // groupMembers.add(GroupMember.create(14L, 7L, Role.MEMBER));  // 경제학 세미나 멤버

        // 취미 그룹 (8~14번)
        groupMembers.add(GroupMember.create(15L, 8L, Role.LEADER));  // 등산 동호회 리더
        groupMembers.add(GroupMember.create(16L, 8L, Role.MEMBER));  // 등산 동호회 멤버
        groupMembers.add(GroupMember.create(17L, 9L, Role.LEADER));  // 요리 마스터 클럽 리더
        groupMembers.add(GroupMember.create(18L, 9L, Role.MEMBER));  // 요리 마스터 클럽 멤버
        groupMembers.add(GroupMember.create(19L, 10L, Role.LEADER)); // 기타 연주 모임 리더
        groupMembers.add(GroupMember.create(20L, 10L, Role.MEMBER)); // 기타 연주 모임 멤버
        groupMembers.add(GroupMember.create(21L, 11L, Role.LEADER)); // 사진 촬영 여행단 리더
        groupMembers.add(GroupMember.create(22L, 11L, Role.MEMBER)); // 사진 촬영 여행단 멤버
        groupMembers.add(GroupMember.create(23L, 12L, Role.LEADER)); // 독서 모임 리더
        groupMembers.add(GroupMember.create(24L, 12L, Role.MEMBER)); // 독서 모임 멤버
        groupMembers.add(GroupMember.create(25L, 13L, Role.LEADER)); // 원예 가드닝 클럽 리더
        groupMembers.add(GroupMember.create(26L, 13L, Role.MEMBER)); // 원예 가드닝 클럽 멤버
        groupMembers.add(GroupMember.create(27L, 14L, Role.LEADER)); // 보드게임 파티 리더
        groupMembers.add(GroupMember.create(28L, 14L, Role.MEMBER)); // 보드게임 파티 멤버

        // 멘토링 그룹 (15~21번)
        groupMembers.add(GroupMember.create(29L, 15L, Role.LEADER)); // 커리어 멘토링 그룹 리더
        groupMembers.add(GroupMember.create(30L, 15L, Role.MEMBER)); // 커리어 멘토링 그룹 멤버
        groupMembers.add(GroupMember.create(31L, 16L, Role.LEADER)); // 스타트업 창업 가이드 리더
        groupMembers.add(GroupMember.create(32L, 16L, Role.MEMBER)); // 스타트업 창업 가이드 멤버
        groupMembers.add(GroupMember.create(33L, 17L, Role.LEADER)); // 리더십 코칭 클럽 리더
        groupMembers.add(GroupMember.create(34L, 17L, Role.MEMBER)); // 리더십 코칭 클럽 멤버
        groupMembers.add(GroupMember.create(35L, 18L, Role.LEADER)); // IT 취업 준비단 리더
        groupMembers.add(GroupMember.create(36L, 18L, Role.MEMBER)); // IT 취업 준비단 멤버
        groupMembers.add(GroupMember.create(37L, 19L, Role.LEADER)); // 예술가 멘토링 네트워크 리더
        groupMembers.add(GroupMember.create(38L, 19L, Role.MEMBER)); // 예술가 멘토링 네트워크 멤버
        groupMembers.add(GroupMember.create(39L, 20L, Role.LEADER)); // 금융 투자 조언 모임 리더
        groupMembers.add(GroupMember.create(40L, 20L, Role.MEMBER)); // 금융 투자 조언 모임 멤버
        groupMembers.add(GroupMember.create(41L, 21L, Role.LEADER)); // 건강 관리 코칭 리더
        groupMembers.add(GroupMember.create(42L, 21L, Role.MEMBER)); // 건강 관리 코칭 멤버

        // 여러 그룹에 가입한 회원 추가
        groupMembers.add(GroupMember.create(1L, 2L, Role.MEMBER)); // 영어 회화 모임 멤버로 가입
        groupMembers.add(GroupMember.create(1L, 3L, Role.MEMBER)); // 데이터 사이언스 학습단 멤버로 가입

        // 모든 GroupMember 저장
        groupMembers.forEach(groupMemberRepository::save);

        Board board1 = Board.create(1L, BoardType.ASSIGNMENT_SUBMISSION, "test board 1", "test board 1 description");
        boardRepository.save(board1);

        Post post1 = Post.create(1L, 1L, "첫 번째 게시글 제목", "첫 번째 게시글 내용");
        Post post2 = Post.create(1L, 2L, "두 번째 게시글 제목", "두 번째 게시글 내용");
        Post post3 = Post.create(1L, 1L, "세 번째 게시글 제목", "세 번째 게시글 내용");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        log.info("Data init completed.");
    }
}
