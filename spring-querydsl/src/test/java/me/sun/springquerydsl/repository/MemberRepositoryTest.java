package me.sun.springquerydsl.repository;

import me.sun.springquerydsl.MemberSearchCondition;
import me.sun.springquerydsl.MemberTeamDto;
import me.sun.springquerydsl.entity.Member;
import me.sun.springquerydsl.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}