package me.sun.springquerydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import me.sun.springquerydsl.entity.Member;
import me.sun.springquerydsl.entity.QMember;
import me.sun.springquerydsl.entity.QTeam;
import me.sun.springquerydsl.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static me.sun.springquerydsl.entity.QMember.*;
import static me.sun.springquerydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;


    // 시작은 이렇게 한다.
    JPAQueryFactory queryFactory;


    @BeforeEach
    public void init() {

        // 이렇게해도 동시성 문제가 없다.
        // em 자체가 멀티 스레드에 문제 없게 만들어져있다.
        queryFactory = new JPAQueryFactory(em);

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
    }

    @Test
    void startJPQL() throws Exception {

        Member findByJPQL = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findByJPQL.getUsername()).isEqualTo("member1");
    }

    @Test
    void startQueryDsl() throws Exception {
        //given

        /* QueryDsl은 JPQL의 빌더 역할이므로 결국은 JPQL로 동작 된다.

         */
        // 이미 만들어 진것을 사용할 것이다.

        //QMember m = new QMember("m"); 같은 테이블을 조인할 때는 이렇게 엘리어스를 다르게 해줘야 한다.
        //QMember m = QMember.member;

        //when

        // jpql에서는 파라미터 바인딩을 안해준다
        // 이렇게 짜도 자동으로 프리페어 스테이트먼트의 파라미터 바인딩해서 넣어준다.
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        //then

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void search() throws Exception {
        //given
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();
        /*
            eq -> =
            ne -> !=
            eq().net() -> !=

            isNotNull()

            in(10, 20)
            notIn(10, 20)
            between(10, 20)

            goe(20) >=
            gt >
            loe <=
            lt <

            like(data%) -> data%
            contains -> %data%
            startsWith -> data%
         */

        //then
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }


    @Test
    void searchAndParam() throws Exception {
        //given
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        // where and를 쉼표로 구분해도 된다.
                        // null이 들어가면 null을 무시하므로 좀더 유용함
                        member.username.eq("member1"),
                        member.age.eq(10)
                ).fetchOne();

        //then
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /*
        fetch(): 리스트 조회, 없으면 빈 리스트
        fetchOne(): 단건
        fetchFirst() : limit(1)
        fetchResult(): 페이징 포함
        fetchCount() 카운트
     */
    @Test
    void resultFetch() throws Exception {
        List<Member> fetch = queryFactory.selectFrom(member).fetch();

//        Member findMember = queryFactory.selectFrom(member).fetchOne();

        Member firstMember = queryFactory.selectFrom(member).fetchFirst();

        // 토탈 카운트 날리기 위해 쿼리를 두방 날린다.
        // 진짜 성능이 중요할 땐 카운트를 따로 날리는게 좋다.
        QueryResults<Member> results = queryFactory.selectFrom(member).fetchResults();

        long total = results.getTotal();

        List<Member> content = results.getResults();

        // 카운트용 쿼리 날림
        long count = queryFactory.selectFrom(member).fetchCount();

    }

    /**
     * 회원 정렬
     * 1. 회원 나이 내림차순
     * 2. 회원 이름 오름차순
     * 단 2에서 회원 이름 없을 시 마지막에 출력(nulls last)
     */
    @Test
    void sort() throws Exception {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast()) // nullsFirst도 존재
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }


    @Test
    void paging1() throws Exception {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 하나 스킵
                .limit(2) // 2개 가꼬온다
                .fetch();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getUsername()).isEqualTo("member2");
    }

    @Test
    void paging2() throws Exception {

        // 페이징 쿼리가 복잡해지면 카운트는 빠로 짜는게 좋을 수도 있다.
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 하나 스킵
                .limit(2) // 2개 가꼬온다
                .fetchResults();

        assertThat(results.getTotal()).isEqualTo(4);
        assertThat(results.getLimit()).isEqualTo(2);
        assertThat(results.getOffset()).isEqualTo(1);
        assertThat(results.getResults().size()).isEqualTo(2);
    }


    @Test
    void aggregation() throws Exception {

        // select을 짜로 지정하면 tuple로 나온다.
        // tuple이란게 여러개 타입이 있을 때 꺼내올 수 있는것
        List<Tuple> fetch = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member) // select을 따로 지정하면 select , from으로 따로 빼야함
                .fetch();

        Tuple tuple = fetch.get(0);

        // tuple.get에 select에 넣은걸 그대로 쓰면 된다.
        // 실무에선 잘 쓰지않고 DTO로 뽑아온다.
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);

    }

    /**
     * 팀의 이름과 각 팀의 평균을 구해라
     */
    @Test
    void groupBy() throws Exception{
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);


    }



}
