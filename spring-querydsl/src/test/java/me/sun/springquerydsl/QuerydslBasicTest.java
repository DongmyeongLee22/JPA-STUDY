package me.sun.springquerydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import me.sun.springquerydsl.entity.Member;
import me.sun.springquerydsl.entity.QMember;
import me.sun.springquerydsl.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;


    // 시작은 이렇게 한다.
    JPAQueryFactory queryFactory;


    @BeforeEach
    public void init(){

        // 이렇게해도 동시성 문제가 없다.
        // em 자체가 멀티 스레드에 문제 없게 만들어져있다.
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);

        Member member3 = new Member("member3", 10, teamB);
        Member member4 = new Member("member4", 10, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    void startJPQL() throws Exception{

        Member findByJPQL = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findByJPQL.getUsername()).isEqualTo("member1");
    }

    @Test
    void startQueryDsl() throws Exception{
        //given

        // 이미 만들어 진것을 사용할 것이다.
        QMember m = new QMember("m");

        //when

        // jpql에서는 파라미터 바인딩을 안해준다
        // 이렇게 짜도 자동으로 프리페어 스테이트먼트의 파라미터 바인딩해서 넣어준다.
        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();

        //then

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }



}
