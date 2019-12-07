package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
