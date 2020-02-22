package me.sun.springquerydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import me.sun.springquerydsl.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static java.time.LocalDate.*;
import static me.sun.springquerydsl.entity.QDateTest.*;
import static me.sun.springquerydsl.entity.QMember.*;
import static me.sun.springquerydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;


    // 시작은 이렇게 한다.
    @Autowired
    JPAQueryFactory queryFactory;


    @BeforeEach
    public void init() {

        // 이렇게해도 동시성 문제가 없다.
        // em 자체가 멀티 스레드에 문제 없게 만들어져있다.
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
    void groupBy() throws Exception {
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

    /**
     * 팀 A에 소속된 모든 회원
     */
    @Test
    void join1() throws Exception {
        List<Member> teamA = queryFactory
                .selectFrom(member)
                .join(member.team, team)
//                .leftJoin(member.team, team)
//                .rightJoin(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        for (Member member1 : teamA) {
            assertThat(member1.getTeam().getName()).isEqualTo("teamA");
        }

        assertThat(teamA)
                .extracting("username")
                .containsExactly("member1", "member2");
    }


    /**
     * 세타 조인
     * 회원의 이름이 팀 이름과 같은 회원 조회 -> 연관관계가 없는 거 조회해보기
     */
    @Test
    void theta_join() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        /* 모든 회원, 팀 가져와서 비교하는 것처럼 막 조인해버리는게 세타조인
            -> 외부조인(left, right outer join)이 불가하고 내부 조인만 가능
            -> 최신 버전이 들어오면서 외부 조인도 가능한 방법이 있다. 아래의 조인 on을 다룬다.
        */
        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /** 조인 - on 절 활용
     *  -> 조인 대상 필터링
     *  -> 연관관계 없는 엔티티 외부 조인
     */

    /**
     * 1. 조인대상 필터링
     * 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * <p>
     * JPQL: select m, t from Member m left join m.team t on t.name = 'teamA';
     */
    @Test
    void join_on_filterling() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                // 그냥 inner join이면 굳이 on절이 필요없고 where 가능해진다.
                // on절은 외부조인이 필요한 경우에 쓰자
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {

            /* left 조인이므로 member는 다 가져오고 team은 teamA인 애들만 가져온다.

                tuple = [Member(id=3, username=member1, age=10), Team(id=1, name=teamA)]
                tuple = [Member(id=4, username=member2, age=20), Team(id=1, name=teamA)]
                tuple = [Member(id=5, username=member3, age=30), null]
                tuple = [Member(id=6, username=member4, age=40), null]

               --- JPQL ---

                select
                    member1,
                    team
                from
                    Member member1
                left join
                    member1.team as team with team.name = ?1

               --- 실제 SQL ---

                select
                    member0_.member_id as member_i1_0_0_,
                    team1_.team_id as team_id1_1_1_,
                    member0_.age as age2_0_0_,
                    member0_.team_id as team_id4_0_0_,
                    member0_.username as username3_0_0_,
                    team1_.name as name2_1_1_
                from
                    member member0_
                left outer join
                    team team1_
                        on member0_.team_id=team1_.team_id
                        and (
                            team1_.name=?
                        )
             */

            System.out.println("tuple = " + tuple);
        }
    }


    /**
     * 2. 연관관계가 없는 엔티티 외부 조인
     * 회원의 이름과 팀 이름이 같은 대상 외부조인
     */
    @Test
    void join_on_on_relation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                // .leftJoin(member.team, team) 이렇게 들어가는데아닌 team하나만 들어감! 주의 해야한다.
                // 바로 team을 넣어버리면 id로 매칭하는게아닌 team자체로 조인을 한다. , on절은 필터링하는 것
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            /* 멤버들이 다 나오는데 멤버 이름과 팀이름이 같은 경우에만 팀을 가져온다.

                tuple = [Member(id=5, username=member3, age=30), null]
                tuple = [Member(id=6, username=member4, age=40), null]
                tuple = [Member(id=7, username=teamA, age=0), Team(id=1, name=teamA)]
                tuple = [Member(id=8, username=teamB, age=0), Team(id=2, name=teamB)]


                SQL을 보면 on안에 id로 매칭하는게 아닌 name 자체로 매칭하는것을 알 수 있다.

                left outer join
                   team team1_
                   on (
                       member0_.username=team1_.name
                   )
             */
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 페치조인
     * - sql을 활용해서 연관된 엔티티를 한번에 가져오는것
     * - 성능최적화에서 주로 사용한다.
     */

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // 이미 로딩된 엔티티인지 확인할 수 있다.
        boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(isLoaded).as("페치 조인 미 적용").isFalse();
    }

    @Test
    void fetchJoinYes() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        // 이미 로딩된 엔티티인지 확인할 수 있다.
        boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(isLoaded).as("페치 조인 적용").isTrue();
    }

    /* ================================ JPA Expressions 서브 쿼리를 사용할 수 있다. ================================ */

    /**
     * - 나이가 가장 많은 회원 조회
     */
    @Test
    void subQuery() throws Exception {

        QMember subMember = new QMember("subMember");

        //given
        List<Member> fetch = queryFactory.selectFrom(member)
                .where(member.age.eq(
                        select(subMember.age.max())  // static import 사용했다
                                .from(subMember)
                ))
                .fetch();
        //then

        assertThat(fetch.size()).isEqualTo(1);

        assertThat(fetch).extracting("age").containsExactly(40);
    }


    /**
     * - 나이가 평균 이상 인 회원 조회
     */
    @Test
    void subQueryGoe() throws Exception {

        QMember subMember = new QMember("subMember");

        //given
        List<Member> fetch = queryFactory.selectFrom(member)
                .where(member.age.goe(
                        select(subMember.age.avg())
                                .from(subMember)
                ))
                .fetch();
        //then

        assertThat(fetch.size()).isEqualTo(2);

        assertThat(fetch).extracting("age").containsExactly(30, 40);
    }

    /**
     * - 나이가 10 살보다 큰 사람들 조회
     */
    @Test
    void subQueryIn() throws Exception {

        QMember subMember = new QMember("subMember");

        //given
        List<Member> fetch = queryFactory.selectFrom(member)
                .where(member.age.in(
                        select(subMember.age)
                                .from(subMember)
                                .where(subMember.age.gt(10))
                ))
                .fetch();
        //then

        assertThat(fetch.size()).isEqualTo(3);

        assertThat(fetch).extracting("age").containsExactly(20, 30, 40);
    }

    /**
     * - select도 subquery 가능하다
     */
    @Test
    void selectSubQuery() throws Exception {
        QMember subMember = new QMember("subMember");

        List<Tuple> fetch = queryFactory
                .select(member.username,
                        select(subMember.age.avg())
                                .from(subMember))
                .from(member)
                .fetch();
        /*
            tuple = [member1, 25.0]
            tuple = [member2, 25.0]
            tuple = [member3, 25.0]
            tuple = [member4, 25.0]
         */

        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
    }

    /** JPQL의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다.
     *  - 원래 select도 안되지만 하이버네이트가 되게 해준 것..
     *
     *** 해결 방안
     *   - 1. 대부분 from절 서브쿼리는 join으로 바꾸어 진다.
     *   - 2. 그게 안된다면 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
     *   - 3. nativeSQL을 사용한다.
     *   - // from 절에 많은 서브쿼리를 사용하려한다면 쿼리 작성에 대해 한번 생각해보자/
     */


    /**
     * CASE 문
     * - 왠만하면 디비는 실제 데이터를 조회하는 위주로 사용하는게 좋다.
     * - 아래와 같은것은 그냥 예제용이지 실제는 일단 값을 받아와서 앱에서 처리하자
     */
    @Test
    void basicCase() throws Exception {
        //given
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();
        queryFactory
                .selectFrom(member)
                .from(member)
                .fetch().forEach(System.out::println);
        //then
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void complexCase() throws Exception {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0 ~ 20 살")
                        .when(member.age.between(21, 30)).then("21 ~ 30 살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * 상수, 문자 더하기
     */
    @Test
    void constant() throws Exception {
        //given

        // JPQL에서는 상수가 안나간다
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    void concat() throws Exception {

        //{username}_{age}
        List<String> result = queryFactory

//                .select(member.username.concat("_").concat(member.age)) age는 숫자이므로 concat안된다.
                // enum, 숫자 등은 stringValue로 문자로 변환해주는게 좋다.
                // 특히 enum 처리할 때 많이 사용하게 된다.
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch();
        /*
        select
            ((member0_.username||?)||cast(member0_.age as char)) as col_0_0_  SQL을 보면 age를 char로 캐스팅 한것을 알 수 있다.
        from
            member member0_
         */

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void monthTest() throws Exception {
        //given

        DateTest dateTest1 = new DateTest("name1", of(2014, 11, 11), of(2014, 11, 20));

        DateTest dateTest3 = new DateTest("name2", of(2014, 10, 11), of(2014, 10, 20));
        DateTest dateTest4 = new DateTest("name2", of(2014, 10, 11), of(2014, 11, 20));
        DateTest dateTest5 = new DateTest("name2", of(2014, 12, 11), of(2014, 12, 20));
        DateTest dateTest6 = new DateTest("name2", of(2014, 11, 11), of(2014, 12, 20));

        DateTest dateTest2 = new DateTest("name3", of(2014, 11, 11), of(2014, 12, 20));
        em.persist(dateTest1);
        em.persist(dateTest2);
        em.persist(dateTest3);
        em.persist(dateTest4);
        em.persist(dateTest5);
        em.persist(dateTest6);
        em.flush();
        em.clear();

//        List<DateTest> twoGet = queryFactory
//                .selectFrom(dateTest)
//                .where(dateTest.start.month().eq(11))
//                .fetch();
//
//        List<DateTest> threeGet = queryFactory
//                .selectFrom(dateTest)
//                .where(dateTest.start.month().eq(11).or(dateTest.end.month().eq(11)))
//                .fetch();

        List<DateTest> findDate = queryFactory
                .selectFrom(dateTest)
                .where(dateTest.name.eq("name2").and(
                        dateTest.start.month().eq(11).
                                or(dateTest.end.month().eq(11))
                ))
                .fetch();

//        for (DateTest dateTest : twoGet) {
//            System.out.println("dateTest.getName() = " + dateTest.getName());
//        }
//
//        System.out.println(
//
//        );
//
//        for (DateTest dateTest : threeGet) {
//            System.out.println("dateTest.getName() = " + dateTest.getName());
//        }
//
//        System.out.println();

        for (DateTest dateTest : findDate) {
            System.out.println("dateTest = " + dateTest.getName());
            System.out.println("dateTest.getStart() = " + dateTest.getStart());
            System.out.println("dateTest.getEnd() = " + dateTest.getEnd());
        }

    }


}
