package me.sun.springquerydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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
import java.util.List;

import static me.sun.springquerydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicIntermideateTest {

    @Autowired
    private EntityManager em;


    // 시작은 이렇게 한다.
    private JPAQueryFactory queryFactory;


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

    /* =============================== 중급 문법 =============================== */

    /**
     * - 프로젝션 대상이 하나면 명확하게 지정 가능
     * - 프로젝션 대상이 둘 이상이면 DTO나 튜플로 조회가능
     * - 튜플은 여러개의 데이터 타입을 사용할 수 있는 타입
     */
    // 프로젝션 대상이 하나
    @Test
    void simpleProjection() throws Exception {
        //given
        List<String> userName = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String name : userName) {
            System.out.println("userName = " + name);
        }

        List<Member> member1 = queryFactory
                .select(member)
                .from(member)
                .fetch();

        for (Member member : member1) {
            System.out.println("member = " + member);
        }

    }

    /*  프로젝션 대상이 둘 이상일 때는 튜플로 조회한다.
     *  - 튜플은 repository 계층에만 사용하자
     *  - 서비스 단까지 올리면 좋지 않은 설계이다.
     *  - 서비스에 전달할 때는 DTO로 바꿔서 꼭 반환해주자.
     */

    @Test
    void tupleProjection() throws Exception {
        //given
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String userName = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("userName = " + userName);
            System.out.println("age = " + age);
        }
    }


    /**
     * DTO로 조회해보기
     */

    @Test
    void findDtoByJPQL() throws Exception {
        // jpql은 패키지명 까지 다 작성해서 가져와야한다.
        // 그리고 생성자로만 가능하다.
        List<MemberDto> resultList = em
                .createQuery("select new me.sun.springquerydsl.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();
        for (MemberDto memberDto : resultList) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /*
        QueryDsl은 프로퍼티 접근, 필드 접근, 생성자 사용 모두 가능하다.
     */

    // 프로퍼티 접근(setter)로 접근하기
    @Test
    void findDtoByQueryDsl() throws Exception {

        // 기본 생성자가 없으면 에러난다.
        List<MemberDto> resultList = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : resultList) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // 필드로 접근하기
    // get, setter 없어도 필드로 바로 접근하므로 괜찮음
    @Test
    void findDtoByQueryDslFields() throws Exception {


        // fieds는 바로 값이 필드에 꽂힌다.
        List<MemberDto> resultList = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : resultList) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // 생성자로 접근하기
    @Test
    void findDtoByQueryDslConstructor() throws Exception {


        // constructor를 사용하면 된다.
        // 생성자와 매개변수 타입이 일치해야 한다.
        List<MemberDto> resultList = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : resultList) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // Dto의 필드명이 다를 때
    @Test
    void findDtoByQueryDslFieldsByUserDto() throws Exception {


        // DTO의 필드명이랑 다르면 조회가 안된다.
        List<UserDto> resultList = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        // 그러므로 name은 null이 나옴
        for (UserDto userDto : resultList) {
            System.out.println("memberDto = " + userDto);
        }

        // as를 사용하면 가능하다
        List<UserDto> resultList2 = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        member.age))
                .from(member)
                .fetch();

        for (UserDto userDto : resultList2) {
            System.out.println("memberDto = " + userDto);
        }


        // 서브쿼리 사용
        // 자주 쓰진 않지만 알아두면 좋음
        QMember memberSub = new QMember("memberSub");
        List<UserDto> resultList3 = queryFactory
                .select(Projections.fields(UserDto.class,
                        // ExpressionUtils.as(member.username, "name"), 이거도 되지만 서브 쿼리가 아니라면 할 이유가 없음
                        member.username.as("name"),

                        // 서브 쿼리로 age max를 선택하고 alias를 주면 이렇게도 가능
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub), "age")
                ))
                .from(member)
                .fetch();

        for (UserDto userDto : resultList3) {
            System.out.println("memberDto = " + userDto);
        }
    }

    // 생성자는 타입으로 접근하기 때문에 필드명이 달라도 가능
    @Test
    void findDtoByQueryDslConstructorWithUserDto() throws Exception {


        // constructor를 사용하면 된다.
        // 생성자와 매개변수 타입이 일치해야 한다.
        List<UserDto> resultList = queryFactory
                .select(Projections.constructor(UserDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (UserDto memberDto : resultList) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 프로젝션과 결과 반환
     * - @QueryProejct을 DTO에 넣으면 간단하게 dto를 생성할 수 있다
     * - 가장 깔끔한 방법이나 단점이 좀 있다.
     * - DTO 생성자에 annotation 붙이고 컴파일하면 Q타입 생겨남
     */

    /* 생겨난 Q 타입으로 select을 사용하면 된다.
       - 실무에서 고민거리
         * 컴파일시점에 타입 체크가 되니 가장 안전한 방법
         * 단점1. Q파일 생성해야함..
         * 단점2. dto는 querydsl에 대한 의존관계가 없었지만 의존성이 생김
            - 만약 querydsl 라이브러리를 제거한다면 dto가 다 영향을 받는다.
            - 그리고 dto는 서비스, api 반환 등 여러 레이어에 거쳐서 반환하게 된다.
            - dto가 순수하지 않고 querydsl에 의존적이다..
            - 아키텍쳐적으로 dto를 깔끔하게 가져가고 싶다면 필드나, 빈, 생성자 방식을 쓰자

     */
    @Test
    void findDtoByQueryProejction() throws Exception {

        // 이 방식을 쓰면 위의 생성자 방식보다 좋은 이유는 컴파일 시점에 에러를 잡아준다.
        // 생성자는 런타임시에 에러가 발생한다..
        List<MemberQueryProejctDto> result = queryFactory
                .select(new QMemberQueryProejctDto(member.username, member.age))
                .from(member)
                .fetch();

        for (MemberQueryProejctDto dto : result) {
            System.out.println("dto = " + dto);
        }
    }

    /* ======================================== 프로젝션과 결과 반환 끝 ======================================== */


}
