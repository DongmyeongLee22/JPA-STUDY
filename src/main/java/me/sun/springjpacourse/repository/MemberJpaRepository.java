package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.entity.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    //JPA의 em을 가져다 준거다.
    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public Optional<Member> findById(Long memberId) {
        Member member = em.find(Member.class, memberId);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }

    public Member find(Long memberId) {
        return em.find(Member.class, memberId);
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    /**
     * JPA 페이징 해보기
     * 검색 조건: 나이가 10살
     * 정렬 조건: 이름으로 내림차순
     * 페이징 조건: 첫 번째 페이지, 페이지당 보여줄 데이터는 3건
     */
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc", Member.class)
                .setParameter("age", age)
                .setFirstResult(offset)  // 몇번째 부터 ?
                .setMaxResults(limit) // 개수 몇개?
                .getResultList();
    }

    //페이지 번호를 알기 위해 카운트를 가져올땐 정렬이 필요 없다.
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

}
