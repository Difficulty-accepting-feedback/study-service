package com.grow.study_service.common.init;

import com.grow.study_service.group.domain.enums.Category;
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

@Slf4j
// @Component
@AllArgsConstructor
public class DataInit implements CommandLineRunner {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PostRepository postRepository;

    @Override
    public void run(String... args) throws Exception {
        Group group1 = Group.create("test group 1", Category.STUDY, "test group 1 description");
        groupRepository.save(group1);

        GroupMember groupMember = GroupMember.create(1L, 1L, Role.MEMBER);
        GroupMember groupLeader = GroupMember.create(2L, 1L, Role.LEADER);

        groupMemberRepository.save(groupMember);
        groupMemberRepository.save(groupLeader);

        Post post1 = Post.create(1L, 1L, "첫 번째 게시글 제목", "첫 번째 게시글 내용");
        Post post2 = Post.create(1L, 2L, "두 번째 게시글 제목", "두 번째 게시글 내용");
        Post post3 = Post.create(1L, 1L, "세 번째 게시글 제목", "세 번째 게시글 내용");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        log.info("Data init completed.");
    }
}
