package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.entity.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberJpaRepository {

    //JPA의 em을 가져다 준거다.
    @PersistenceContext
    private EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public Member find(Long memberId){
        return em.find(Member.class, memberId);
    }
}
