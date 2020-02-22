package me.sun.springquerydsl.repository;

import me.sun.springquerydsl.MemberSearchCondition;
import me.sun.springquerydsl.MemberTeamDto;
import me.sun.springquerydsl.entity.Member;
import me.sun.springquerydsl.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberJPARepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJPARepository memberJPARepository;

    /**
     * 순수 JPQ Repository랑 Query Dsl 같이 사용
     */
    @Test
    void basicTest() throws Exception {
        //given
        Member member = new Member("mebmer1", 10);
        memberJPARepository.save(member);


        Member findMember = memberJPARepository.findBYId(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> findAllMember = memberJPARepository.findAll();
        assertThat(findAllMember).containsExactly(member);

        List<Member> findMemberByUsername = memberJPARepository.findByUsername("mebmer1");
        assertThat(findMemberByUsername).containsExactly(member);
    }

    @Test
    void basicQueryDslTest() throws Exception {
        Member member = new Member("mebmer1", 10);
        memberJPARepository.save(member);

        List<Member> findAllMember = memberJPARepository.findAll_QueryDsl();
        assertThat(findAllMember).containsExactly(member);

        List<Member> findMemberByUsername = memberJPARepository.findAll_ByUsername_QueryDsl("mebmer1");
        assertThat(findMemberByUsername).containsExactly(member);

    }

    @Test
    void searchTest2() throws Exception {
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

        List<MemberTeamDto> memberTeamDtos = memberJPARepository.searchByBuilder(condition);

        assertThat(memberTeamDtos).extracting("username").containsExactly("member4");
    }

    @Test
    void searchTest() throws Exception {
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

        /** 조건이 다 빠지면 모든 값을 조회한다,
         *  - 데이터가 많을 떄 매우 안좋음
         *  - 그렇기 떄문에 기본조건을 넣던지 페이징 하는게 좋다.
         */
        MemberSearchCondition condition = new MemberSearchCondition();

        List<MemberTeamDto> memberTeamDtos = memberJPARepository.searchByBuilder(condition);

        assertThat(memberTeamDtos).extracting("username").containsExactly("member4");
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

        List<MemberTeamDto> memberTeamDtos = memberJPARepository.search(condition);

        assertThat(memberTeamDtos).extracting("username").containsExactly("member4");
    }
}