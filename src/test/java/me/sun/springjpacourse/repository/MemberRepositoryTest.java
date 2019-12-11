package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.dto.MemberDto;
import me.sun.springjpacourse.entity.Member;
import me.sun.springjpacourse.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() throws Exception {
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        count = memberRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThenTest() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findHelloBy() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> all = memberRepository.findHelloBy();

        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    public void findTop3By() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        Member member3 = new Member("AAA", 20);
        Member member4 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        List<Member> all = memberRepository.findAll();
        List<Member> top3 = memberRepository.findTop3By();

        assertThat(top3.size()).isEqualTo(3);
        assertThat(all.size()).isEqualTo(4);
    }

    @Test
    public void namedQueryTest() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> find = memberRepository.findByUsername("AAA");

        assertThat(find.get(0).getAge()).isEqualTo(10);
        assertThat(find.get(0).getUsername()).isEqualTo("AAA");

    }

    @Test
    public void findUserTest() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> user = memberRepository.findUser(member1.getUsername(), member1.getAge());

        assertThat(user.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUsernameListTest() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();

        assertThat(usernameList.get(0)).isEqualTo("AAA");
        assertThat(usernameList.get(1)).isEqualTo("BBB");
    }

    @Test
    public void findMemberDtoTest() throws Exception {

        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member member1 = new Member("AAA", 10, teamA);
        memberRepository.save(member1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        assertThat(memberDto.get(0).getTeamname()).isEqualTo("teamA");
        assertThat(memberDto.get(0).getUsername()).isEqualTo("AAA");

    }

    @Test
    public void fundByNames() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> memberList = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        assertThat(memberList.size()).isEqualTo(2);

        assertThat(memberList.get(0).getUsername()).isEqualTo("AAA");
        assertThat(memberList.get(1).getUsername()).isEqualTo("BBB");

    }

    @Test
    public void returnType() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> list = memberRepository.findListByUsername("AAA");
        assertThat(list.get(0).getUsername()).isEqualTo("AAA");

        Member memberB = memberRepository.findMemberByUsername("BBB");
        assertThat(memberB.getUsername()).isEqualTo("BBB");

        Optional<Member> optionalB = memberRepository.findOptionalByUsername("BBB");
        Member getMember = optionalB.get();
        assertThat(getMember.getUsername()).isEqualTo("BBB");


        // List는 없으면 empty가 반환되지 null이 아니다!!!
        List<Member> emptyCollection = memberRepository.findListByUsername("AQWEADAA");
        System.out.println("===============" + emptyCollection.toString());
        assertThat(emptyCollection).isNotNull();
        assertThat(emptyCollection).isEmpty();
        assertThat(emptyCollection.size()).isEqualTo(0);


        // 단건일 경우 NULL이다.
        Member nullMember = memberRepository.findMemberByUsername("QWDQDQ");
        assertThat(nullMember).isNull();

        // Optional은 원래 Optional처럼 된다.
        Optional<Member> optionalMem = memberRepository.findOptionalByUsername("QWDWDQD");
        assertThat(optionalMem).isEmpty();

        Member member3 = new Member("AAA", 10);
        memberRepository.save(member3);

        /*
         NonUniqueResultException == > IncorrectResultSizeDataAccessException(스프링 프레임 워크 예외)
         예외를 스프링 예외로 반환해준다. 왜냐하면 리포지토리 기술은 JPA가 될수도 있지만 MongoDB가 될수도 있고 등등
         다른 기술들이 될 수 있기 때문에 동일한 예외를 던저주기 위해서 그런거다.
         */
        Optional<Member> twoAAAMember = memberRepository.findOptionalByUsername("AAA");

    }

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;

        // 스프링 데이터 JPA는 페이지를 1이 아닌 0부터 시작한다.
        // 정렬은 넣어도되고 안 해도되고
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> pagedMember = memberRepository.findByAge(age, pageRequest);

        // 반환 타입을 Page로 받으면 total Count Query까지 같이 날리기 때문에 total Count를 따로 구할 필요가 없다.
        // 카운트 쿼리 날라가는거 확인

        //then
        List<Member> content = pagedMember.getContent();
        long totalElements = pagedMember.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        // page 번호까지 받을 수 있다!!
        assertThat(pagedMember.getNumber()).isEqualTo(0);
        assertThat(pagedMember.getTotalPages()).isEqualTo(2);
        assertThat(pagedMember.isFirst()).isTrue();
        assertThat(pagedMember.hasNext()).isTrue();

    }

    @Test
    public void slicing() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when

        //findByAge는 Repository를 보면 Page이다. Page가 Slice를 상속받아 에러가 안나지만 주의해야 한다.
//        Slice<Member> pagedMember = memberRepository.findByAge(age, pageRequest);

        // Slice는 카운트 쿼리를 안날린다.
        // limit 숫자를 보면 3이아닌 4개이다. 이것은 요즘 인터넷에서 많이 사용되는 더보기 방식을 위해 있는것이다.
        // 11개를 받아온 후 유저에게 10개만 보여주고 1개만 숨긴 후 더보기 버튼을 만든다. 유저가 더보기 버튼을 누르면
        // 그때 받아오는 방식??
        Slice<Member> pagedMember = memberRepository.findSliceByAge(age, pageRequest);


        //then
        List<Member> content = pagedMember.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(pagedMember.getNumber()).isEqualTo(0);
        assertThat(pagedMember.isFirst()).isTrue();
        assertThat(pagedMember.hasNext()).isTrue();

    }

    @Test
    public void getListUsingPage() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // 이렇게 페이징하지 않고 그냥 처음 갯수만 받을 수도 있다.
        List<Member> pagedMember = memberRepository.findListByAge(age, pageRequest);

        //then
        assertThat(pagedMember.size()).isEqualTo(3);
    }

    @Test
    public void Top3Test() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        //when
        List<Member> top3By = memberRepository.findTop3By();

        //then
        assertThat(top3By.size()).isEqualTo(3);
    }


    @Test
    public void bulkUpdate() throws Exception {

        //given
        Member member3 = new Member("member3", 20);
        Member member4 = new Member("member4", 30);

        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(new Member("member5", 40));

        //when

        /*
        JPA는 영속성 컨텍스트에서 엔티티가 관리가 되는데 벌크연산은 그것을 다 무시하고 DB에 때리기 때문에
        이렇게 bulk를 날리면 영속성 컨텍스트의 내용이 반영이 안됨. 그렇기 때문에 em.flush와 clear 해줘야한다.
         */
//        em.flush(); em.clear();// 이거 해주기 싫으면 @Modifying에 설정
        int resultCount = memberRepository.bulkAgePlus(20);
        // 가장 깔끔한건 영속성 컨텍스트에 없고 벌크 날리는 것.

        /*
        JPA와 JDBC or 마이바티스등 이랑 같이 쓸때 이것을 조심해야 한다.
        벌크 연산을 쏜거랑 마이바티스에서 직접 날리는것은 JPA가 인식을 못하므로 영속성 컨텍스트 값이 맞지 않을 수 있다.
        그러므로 flush, clear를 해줘야 한다.
         */
        //then
        Member findMember4 = memberRepository.findById(member4.getId()).get();
        Member findMember3 = memberRepository.findById(member3.getId()).get();

        assertThat(resultCount).isEqualTo(3);
        assertThat(findMember3.getAge()).isEqualTo(21);
        assertThat(findMember4.getAge()).isEqualTo(31);

    }
}