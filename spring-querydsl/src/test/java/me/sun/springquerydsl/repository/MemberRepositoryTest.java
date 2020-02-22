package me.sun.springquerydsl.repository;

import me.sun.springquerydsl.MemberSearchCondition;
import me.sun.springquerydsl.MemberTeamDto;
import me.sun.springquerydsl.entity.Member;
import me.sun.springquerydsl.entity.QMember;
import me.sun.springquerydsl.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Stranger on 2020/02/22
 */
@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;


    @Test
    void basicTest() throws Exception{
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        //when
        List<Member> findMembers = memberRepository.findByUsername(member.getUsername());

        //then
        assertThat(findMembers.size()).isEqualTo(1);
        assertThat(findMembers).extracting("username").containsOnly("member1");
    }

    @Test
    void searchTest2Where() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> memberTeamDtos = memberRepository.search(condition);

        assertThat(memberTeamDtos).extracting("username").containsExactly("member4");
    }

    @Test
    void searchPageSimple() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();

        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest);

        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).extracting("username").containsOnly("member1", "member2", "member3");
    }

    @Test
    void querydslPredicateExecutorTest() throws Exception{
        // given

        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);

        Member member3 = new Member("member3", 30);
        Member member4 = new Member("member4", 40);

        /**
         * 스프링 데이터 JPA가 지원하는 기능중 하나다.
         * QueryPredicateExecutor를 상속받아 이렇게 사용할 수도 있다.
         * - 실무에서는 조인이 매우 많다.
         * - 하지만 이 방법은 조인이 불가능하다.
         * - 그리고 클라이언트 코드가 querydsl에 의존해야한다.
         */
        QMember qmember = QMember.member;
        Iterable<Member> iterable = memberRepository.findAll(qmember.age.between(10, 40).and(qmember.username.eq("mebmer1")));
        for (Member member : iterable) {
            assertThat(member.getUsername()).isEqualTo("member1");
        }
        //when
        //then
    }
}