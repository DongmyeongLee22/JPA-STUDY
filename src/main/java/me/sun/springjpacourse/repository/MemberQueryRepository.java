package me.sun.springjpacourse.repository;

import lombok.RequiredArgsConstructor;
import me.sun.springjpacourse.entity.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/*
아예 새로운 리포지토리를 만들어서 사용한다.

** 실무에서 클래스를 분리하기 **
1. Custom에 다 넣는게 아닌 커맨드와 쿼리를 분리하기
2. 핵심 비즈니스 로직과 핵심이 아닌것(단순 복잡한 화면 같은 것들)을 분리하기
3. 라이프사이클에 따라 무엇을 변경해야하는지 달라지는 것
--> 복잡해질수록 이런것들을 고려하면서 클래스를 쪼개야 한다.
 */
@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {


    private EntityManager em;

    List<Member> findAllMember() {
        return em.createQuery("select m From Member m", Member.class).getResultList();
    }
}
