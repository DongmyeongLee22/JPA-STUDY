package me.sun.springquerydsl.repository;

import me.sun.springquerydsl.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

/**
 * Created by Stranger on 2020/02/22
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, QuerydslPredicateExecutor<Member> {
    List<Member> findByUsername(String username);
}
