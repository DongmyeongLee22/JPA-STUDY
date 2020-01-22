package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findMemberCustom();
}
