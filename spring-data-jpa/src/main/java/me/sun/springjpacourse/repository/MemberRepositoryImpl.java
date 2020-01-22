package me.sun.springjpacourse.repository;

import lombok.RequiredArgsConstructor;
import me.sun.springjpacourse.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/*
뒤에 Impl만 된다!!
딴거 적으면 에러남..
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
