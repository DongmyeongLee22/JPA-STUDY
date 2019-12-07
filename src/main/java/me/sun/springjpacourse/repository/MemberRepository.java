package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
}
