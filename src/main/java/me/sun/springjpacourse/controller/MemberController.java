package me.sun.springjpacourse.controller;

import lombok.RequiredArgsConstructor;
import me.sun.springjpacourse.entity.Member;
import me.sun.springjpacourse.repository.MemberRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    /* ========================== Web 확장 - 도메인 클래스 컨버터 ========================== */

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /*
    이렇게도 된다!! 왜??
    스프링 데이터 JPA가 해주는 것.. 원래는 몇가지 설정이 필요하나 SpringBoot라서 그냥 되는 것

    실무에선 이런 기능을 권장하지 않는다. 왜?
    PK인 id로 데이터를 통신하는 경우는 잘 없을 뿐더러 단순한 쿼리로 돌아가는 경우도 없다.
    간단할때만 사용이 가능하며 복잡해지면 사용할 수 없다.

    HTTP요청은 회원 id를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환한다.

    -주의-
     이렇게 사용하면 단순 조회용으로 사용해야한다.(트랙잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB 반영 X)
     */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }


    @PostConstruct
    public void init() {
        memberRepository.save(new Member("member1"));
    }
}
