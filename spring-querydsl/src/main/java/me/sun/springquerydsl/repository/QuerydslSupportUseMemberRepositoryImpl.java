//package me.sun.springquerydsl.repository;
//
//import com.querydsl.core.QueryResults;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQuery;
//import me.sun.springquerydsl.MemberSearchCondition;
//import me.sun.springquerydsl.MemberTeamDto;
//import me.sun.springquerydsl.QMemberTeamDto;
//import me.sun.springquerydsl.entity.Member;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
//import org.springframework.data.repository.support.PageableExecutionUtils;
//
//import java.util.List;
//
//import static me.sun.springquerydsl.entity.QMember.member;
//import static me.sun.springquerydsl.entity.QTeam.team;
//import static org.springframework.util.StringUtils.hasText;
//import static org.springframework.util.StringUtils.isEmpty;
//
///**
// * Created by Stranger on 2020/02/22
// */
//
///**
// * MemberRepositoryImpl은 규칙이다.
// */
//public class QuerydslSupportUseMemberRepositoryImpl extends QuerydslRepositorySupport implements MemberRepositoryCustom {
//    public QuerydslSupportUseMemberRepositoryImpl() {
//        super(Member.class);
//    }
//
//    /**
//     * 만약 검색이 너무 복잡하고 특정 API나 뷰에 특화된 기능이라면 아예 따로 만들어서 사용하는것도 하나의 방법이 될 수있다.
//     * 중요한 것은 핵심 비즈니스 로직으로 재사용 가능성이 있는 것들, 혹은 엔티티트를 검색하는 특화된 경우는 MemberRepository에 넣는다.
//     * 특정 API에 종속되어 있다면 별도로 MemberQueryRepository를 만들어 사용하는게 나을 수 있다.
//     */
//    public List<MemberTeamDto> search(MemberSearchCondition condition) {
//
//        from(member)
//                .leftJoin(member.team, team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                )
//                .select(new QMemberTeamDto(
//                        member.id.as("memberId"),
//                        member.username,
//                        member.age,
//                        team.id.as("teamId"),
//                        team.name.as("teamName"))
//                )
//                .fetch();
//
//        return queryFactory
//                .select(new QMemberTeamDto(
//                        member.id.as("memberId"),
//                        member.username,
//                        member.age,
//                        team.id.as("teamId"),
//                        team.name.as("teamName")))
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                )
//                .fetch();
//    }
//
//    // Simple ::: 데이터 내용과 카운터를 한번에 조회하기
//    @Override
//    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
//        QueryResults<MemberTeamDto> results = queryFactory
//                .select(new QMemberTeamDto(
//                        member.id.as("memberId"),
//                        member.username,
//                        member.age,
//                        team.id.as("teamId"),
//                        team.name.as("teamName")))
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                )
//                .offset(pageable.getOffset()) // pageable 정보를 통해 paging 값을 넣자
//                .limit(pageable.getPageSize())
//                .fetchResults(); // fetchResults를 사용하면 fetch와 다르게 컨텐츠, 카운터 쿼리를 두번 날린다.
//
//        from(member)
//                .leftJoin(member.team, team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                )
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .select(new QMemberTeamDto(
//                        member.id.as("memberId"),
//                        member.username,
//                        member.age,
//                        team.id.as("teamId"),
//                        team.name.as("teamName")))Z
//                .fetchResults();
//
//        List<MemberTeamDto> content = results.getResults();
//        long total = results.getTotal();
//
//        return new PageImpl<>(content, pageable, total);
//    }
//
//    /**
//     * Complex ::: 데이터 내용과 카운터를 따로 조회하기
//     * - Content 쿼리는 복잡하지만 Count 쿼리는 간단하게 할 수 있는 경우가 있다.
//     * - 만약 Simple 같이 한번에 조회하면 Count에도 조건이 붙기 때문에 최적화가 불가능 하다.
//     */
//    @Override
//    public Page<MemberTeamDto> searhPageComplex(MemberSearchCondition condition, Pageable pageable) {
//        List<MemberTeamDto> content = queryFactory
//                .select(new QMemberTeamDto(
//                        member.id.as("memberId"),
//                        member.username,
//                        member.age,
//                        team.id.as("teamId"),
//                        team.name.as("teamName")))
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                )
//                .offset(pageable.getOffset()) // pageable 정보를 통해 paging 값을 넣자
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        long total = queryFactory
//                .select(member)
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                )
//                .fetchCount();
//        return new PageImpl<>(content, pageable, total);
//    }
//
//    /**
//     * 페이징 시 카운터 쿼리를 생략할 수도 있다.
//     * - 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
//     * - 마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 통해 전체 사이즈를 구함)
//     */
//    @Override
//    public Page<MemberTeamDto> searhPageComplex2(MemberSearchCondition condition, Pageable pageable) {
//        List<MemberTeamDto> content = queryFactory
//                .select(new QMemberTeamDto(
//                        member.id.as("memberId"),
//                        member.username,
//                        member.age,
//                        team.id.as("teamId"),
//                        team.name.as("teamName")))
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                )
//                .offset(pageable.getOffset()) // pageable 정보를 통해 paging 값을 넣자
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        JPAQuery<Member> conuntQuery = queryFactory
//                .select(member)
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe())
//                );
//
//        return PageableExecutionUtils.getPage(content, pageable, conuntQuery::fetchCount); // Supplier의 지연 로딩 사용
//    }
//
//    /**
//     * 정렬은 조금만 복잡해져도 Pageable의 Sort를 사용하기 어렵다.
//     * - 루트 엔티티의 범위를 넘어가는 동적 정렬 기능이 필요하면 스프링 데이터 페이징이 제공하는 Sort를 사용하기보다는 파라미터를 직접 받아서 사용하는게 좋다.
//     */
//
//    // 조합을 위해 BooleanExPression 사용
//    private BooleanExpression usernameEq(String usernmae) {
//        return isEmpty(usernmae) ? null : member.username.eq(usernmae);
//    }
//
//    private BooleanExpression teamNameEq(String teamName) {
//        return hasText(teamName) ? team.name.eq(teamName) : null;
//    }
//
//    private BooleanExpression ageGoe(Integer ageGoe) {
//        return ageGoe != null ? member.age.goe(ageGoe) : null;
//    }
//
//    private BooleanExpression ageLoe(Integer ageLoe) {
//        return ageLoe == null ? null : member.age.loe(ageLoe);
//    }
//}
