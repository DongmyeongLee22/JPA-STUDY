package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.dto.MemberDto;
import me.sun.springjpacourse.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

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
}
