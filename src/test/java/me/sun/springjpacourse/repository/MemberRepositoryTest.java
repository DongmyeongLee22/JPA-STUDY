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

    /* @EntityGraph
     * 페치조인을 간단하게 사용할 수 있음
     * 페치 조인 복습
     */
    @Test
    public void findMemberLazy() throws Exception {
        //given

        // member1 ==> team1
        // member2 ==> team2

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when

        /*
        이때는 멤버만 받아온다. 아무리 지연로딩이라도 Team을 null로 나둘순 없다!!
        그렇기 때문에 지연로딩일 경우 Team에 Proxy객체를 넣어서 이 Proxy를 호출할 때 실제 쿼리를 보내게 된다.
         */
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            System.out.println("member = " + member);

            //이때 보면 프록시인것을 알 수 있다.
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());

            //지연 로딩이므로 쿼리가 두번 더 나간다. 1 + N 문제 발생!
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void findMemberUsingFetchJoin() throws Exception {
        //given

        // member1 ==> team1
        // member2 ==> team2

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when

        /*
        페치를 이용하므로 여기서 한번에 Team까지 조회한다.
        DB조인은 조인만 하지만 페치조인은 조인하고 select 절에 데이터를 다 넣어준다.
         */
//        List<Member> members = memberRepository.findMemberFetchJoin();

        /*
        EnityGraph 사용한 것.
        내부적으로 보면 다 페치조인 쓰는것.
         */
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            System.out.println("member = " + member);

            // Team을 보면 프록시가 아닌 순수한 객체이다.
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush(); // 값을 없애는게 아닌 DB에 동기화
        em.clear(); // 값을 다 없앤다. 클리어시 영속성 컨텍스트가 다 날라간다.

        //when
        Member findMember = memberRepository.findById(member1.getId()).get();

        findMember.setUsername("member2");
        em.flush();
        /*
        변경 감지로 인해 알아서 업데이트 쿼리가 날라간다. 플러시를 해야 그 값이 날라감.
        큰 단점이 있는데 변경 감지를 위해서는 원본이 존재(원본을 만듬)해야한다. 그러므로 객체를 두개 관리하게 된다.

        find할때 이 값을 변경하지 않고 조회용으로만 쓰고 싶어도 이미 가져올때 원본(스냅샷)을 만들어 둔다.
        그러므로 Hint같이 다른 방법을 사용해야 한다.

        실무에서 진짜 복잡한거의 성능 문제는 대부분 쿼리를 잘못 날린거지 스냅샷은 그렇게 크게 문제가 안된다.
        그러므로 하나하나 이렇게 최적화는 상황에 맞게 쓰는게 좋다.
         */

        System.out.println("==============================================================");

        //힌트 사용 : readOnly = true 이므로 가져올때 스냅샷을 만들지 않는다. 변경을해도 적용안됨.
        Member findMember2 = memberRepository.findReadOnlyByUsername("member2");
        findMember2.setUsername("member1");
        em.flush();

    }

    @Test
    public void lockTest() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush(); // 값을 없애는게 아닌 DB에 동기화
        em.clear(); // 값을 다 없앤다. 클리어시 영속성 컨텍스트가 다 날라간다.

        //when
        Member findMember = memberRepository.findLockByUsername("member1");

        //쿼리를 보면 자동으로 for update가 붙는것을 알 수있다.
    }


}