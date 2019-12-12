package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.dto.MemberDto;
import me.sun.springjpacourse.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy();

    List<Member> findTop3By();

    /**
     * 메소드명이랑 NamedQuery가 같으면 @Query 생략가능
     * 없다면 메소드 이름으로 자동 생성으로 찾는다.
     * 자주 사용되지 않는 방법.
     * 단 NamedQuery의 가장 큰 장점은 jpql 오타를 컴파일시 잡을 수 있다.
     */
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    /**
     * 이 방법도 컴파일시 에러를 잡아준다.
     * 메서드 이름으로 쿼리 생성은 한 두개의 파라미터일 때 주로 사용하며
     * 길어지면 이 방법이 좋다.
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new me.sun.springjpacourse.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username);

    Member findMemberByUsername(String username);

    Optional<Member> findOptionalByUsername(String username);

    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    List<Member> findListByAge(int age, Pageable pageable);


    /*
    실무에서 카운트 쿼리가 복잡해지면 성능 이슈가 생길수 있다.
    다른 테이블과 left join을 할 때는 굳이 다른 테이블을 같이 조회할 필요가 없이 기존 테이블만 조회하면 된다.
    그런데 Spring Data Jpa의 Page 기능을 사용하면 카운터 쿼리를 날리게 된다.
    그러므로 아래와 같이 countQuery를 분리하여 가져오면 성능을 원할하게 사용할 수 있다.
     */
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findQueryByAge(int age, Pageable pageable);

    /* 벌크성 수정 쿼리 사용
    더티 체킹으로 한건씩 업데이트 하는게 아닌 예를들어 모든 직원의 연봉을 10% 인상한다 할때 벌크성 수정쿼리라고 한다.
     */
    // 이 Annotation이 있어야 기존 jpa의 .executeUpdate를 실행한다. 그렇지 않으면 에러 발생한다.
    // clear = true로 한다면 따로 em.flush, clear를 안날려도 된다.
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    /* ========================== EntityGraph 사용하기 ========================== */

    //페치조인
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    /* EntityGraph : 페치 조인을 편리하게 사용하기
    사실 JPA 표준 스펙 2.2부터 JPA가 제공한다. 그 EntityGraph를 활용해서 사용할 수 있게 해준 것이다.
    JPA의 NamedEntity
     */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //조회는 JPQL로하고 엔티티 그래프 쓰는것도 가능하다.
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 이렇게 회원 조회때 팀도 뽑는 등 유연하게 사용이 가능하다.
//    @EntityGraph("Member.all") //NamedEntityGraph 사용, 잘 사용 안함..
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /* ========================== JPA Hint & Lock ========================== */

    /* JPA Hint
    JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
    실무에서 진짜 복잡한거의 성능 문제는 대부분 쿼리를 잘못 날린거지 스냅샷은 그렇게 크게 문제가 안된다.
    그러므로 하나하나 이렇게 최적화는 상황에 맞게 쓰는게 좋다.
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /* Lock
    select for update
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // 올라가보면 JPA꺼임
    Member findLockByUsername(String username);

    /* ========================== 사용자 정의 레포지토리 구현 ========================== */

    /*
    실무에서 많이 사용하는 방법이다. 스프링 Jpa는 인터페이스로 구현되어있기 때문에 직접 구현하려면
    그 안에 내용을 다 구현해야 하기때문에 다른 방법이 필요하다.

    복잡도를 낮추기 위해서는 핵심 비즈니스로직의 Repo와 화면을 맞춘 Dto 뽑고 뭐 복잡한것들의 Repo와 분리한다.
     */

    /* ========================== Auditing ========================== */
    /*
    엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶을 때 유용하게 사용한다.
    실무에선 등록일, 수정일은 모든 테이블에 적용하고 등록자, 수정자는 상황에 맞게 적용한다.
    즉 등록일, 수정일은 모든 테이블에 등록해야 한다!!

    테이블에서는 하나하나 다 넣어야 하나 객체지향적인 JPA를 상속을 잘 활용하면 간단하게 구현가능하다.
    1. 순수한 JPA 사용 후 -> JpaBaseEntity.class
    2. 스프링 JPA 사용해보자. -> @EnableJpaAuditing 필요
     */


}
