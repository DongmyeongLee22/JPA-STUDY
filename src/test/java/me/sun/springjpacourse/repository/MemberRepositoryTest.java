package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.dto.MemberDto;
import me.sun.springjpacourse.entity.Member;
import me.sun.springjpacourse.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember() throws Exception {
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        count = memberRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThenTest() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findHelloBy() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> all = memberRepository.findHelloBy();

        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    public void findTop3By() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        Member member3 = new Member("AAA", 20);
        Member member4 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        List<Member> all = memberRepository.findAll();
        List<Member> top3 = memberRepository.findTop3By();

        assertThat(top3.size()).isEqualTo(3);
        assertThat(all.size()).isEqualTo(4);
    }

    @Test
    public void namedQueryTest() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> find = memberRepository.findByUsername("AAA");

        assertThat(find.get(0).getAge()).isEqualTo(10);
        assertThat(find.get(0).getUsername()).isEqualTo("AAA");

    }

    @Test
    public void findUserTest() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> user = memberRepository.findUser(member1.getUsername(), member1.getAge());

        assertThat(user.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUsernameListTest() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();

        assertThat(usernameList.get(0)).isEqualTo("AAA");
        assertThat(usernameList.get(1)).isEqualTo("BBB");
    }

    @Test
    public void findMemberDtoTest() throws Exception {

        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member member1 = new Member("AAA", 10, teamA);
        memberRepository.save(member1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        assertThat(memberDto.get(0).getTeamname()).isEqualTo("teamA");
        assertThat(memberDto.get(0).getUsername()).isEqualTo("AAA");

    }

    @Test
    public void fundByNames() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> memberList = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        assertThat(memberList.size()).isEqualTo(2);

        assertThat(memberList.get(0).getUsername()).isEqualTo("AAA");
        assertThat(memberList.get(1).getUsername()).isEqualTo("BBB");

    }
}