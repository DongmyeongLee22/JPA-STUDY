package me.sun.springjpacourse.controller;

import lombok.RequiredArgsConstructor;
import me.sun.springjpacourse.dto.MemberDto;
import me.sun.springjpacourse.entity.Member;
import me.sun.springjpacourse.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    /* ========================== Web 확장 - 페이징과 정렬 ========================== */

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        /*
        findAll의 파라미터에 pageable넣으면 알아서 반환해줌
        우리가 설정한 메서드에도 마지막 파라미터에 넣어주면 된다.

        url끝에 ?page=1 이렇게 페이지 번호 붙여주면 페이징된다.
        기본은 20개씩 나눠서 주지만
        localhost:8080/members?page=3&size=3 이렇게 size를 넣으면
        3개씩 4번째 페이지이니 10, 11, 12 번째를 보여준다

        이게 되는 이유: Pageable을 구현체로 스프링부트가 자동으로 setting을 해준다.
        http 파라미터가 컨트롤러에서 바인딩 될때 pageable이 있으면 pagerequest 객체를 생성해서
        그 값을 채워 인젝션을 해준다.

        localhost:8080/members?page=3&size=3&sort=id,desc&sort=username,desc 이렇게 sort(기본 asc)도 가능하다.

        ==== 값 설정하기 ====
        글로벌 설정 -> yml에서 세팅
        특별한 설정(우선순위 높음) -> @PageableDefault(size = 5, sort = "username")

        ==페이징 정보 둘 이상일 때 == -> 자료 참고
         */

        Page<Member> page = memberRepository.findAll(pageable);
        /*
        절대 네버 엔티티를 그대로 반환하면 된다. 특히 API일 때는 더욱 더 그러면 안된다.
        Entity를 바꾸면 API 스펙이 바뀌게 되므로.. 그러므로 항상 DTO로 반환하자..

        Dto는 Entity를 봐도 된다. Entity는 Dto를 보지 않는게 좋다.
        왜냐면 Entity는 어차피 앱 내에서 공통을 보는 객체이므로..
         */
        Page<MemberDto> pagedMember = page.map(m -> new MemberDto(m.getId(), m.getUsername(), m.getTeam().getName()));

        Page<MemberDto> map = page.map(MemberDto::new); // DTO에서 엔티티이용하면 메소드 레퍼런스로 바꿀수 있다.
        return map;
    }
    /* 페이지를 1부터 시작하기 : 두가지 방법 존재
       1.Pageable을 직접 만들어서 사용하기
       2.spring.data.web.pageable.one-inexc ... yml 설정하기: 한계 존재
        ---> page = 0, 1이 둘다 첫번째 페이지고 나머지는 정상적
        ---> pageable 객체의 데이터들(offset, pageNumber 등등) 여기는 값이 안맞다.
        ---> page = 2 했는데 pageNumber = 1 이고 막 이렇게 되어있다. 이런걸 감안하고 써야한다.
        ---> 권장사항은 그냥 페이지 인덱스를 0부터 사용하거나 커스텀하자.
     */

    @PostConstruct
    public void init() {
        //memberRepository.save(new Member("member1")); web 확장 - 도메인 클래스 컨버터 용

        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("member" + i, i));
        }

    }
}
