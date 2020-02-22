package me.sun.springquerydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import me.sun.springquerydsl.MemberSearchCondition;
import me.sun.springquerydsl.entity.Member;
import me.sun.springquerydsl.repository.support.Querydsl4RepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.sun.springquerydsl.entity.QMember.member;
import static me.sun.springquerydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Created by Stranger on 2020/02/22
 */
@Repository
public class MemberTestRepository extends Querydsl4RepositorySupport {

    public MemberTestRepository() {
        super(Member.class);
    }

    public List<Member> basicSelect() {
        return select(member)
                .from(member)
                .fetch();
    }

    public List<Member> basicSelectFrom() {
        return selectFrom(member)
                .fetch();
    }

    public Page<Member> searchPageByApplyPage(MemberSearchCondition condition, Pageable pageable) {

        // 쿼리 가져오고
        JPAQuery<Member> query = selectFrom(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );

        // 페이지넣은 결과 가져오고
        List<Member> content = getQuerydsl().applyPagination(pageable, query).fetch();

        // 페이지 만들고
        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    public Page<Member> applyPagination(MemberSearchCondition condition, Pageable pageable) {
        Page<Member> result = applyPagination(pageable, query ->
                query.selectFrom(member)
                        .leftJoin(member.team, team)
                        .where(
                                usernameEq(condition.getUsername()),
                                teamNameEq(condition.getTeamName()),
                                ageGoe(condition.getAgeGoe()),
                                ageLoe(condition.getAgeLoe())
                        )
        );
        return result;
    }

    public Page<Member> applyPaginationComplex(MemberSearchCondition condition, Pageable pageable) {
        Page<Member> result = applyPagination(pageable,
                contentQuery -> contentQuery
                        .selectFrom(member)
                        .leftJoin(member.team, team)
                        .where(
                                usernameEq(condition.getUsername()),
                                teamNameEq(condition.getTeamName()),
                                ageGoe(condition.getAgeGoe()),
                                ageLoe(condition.getAgeLoe())
                        ),
                countQuery -> countQuery
                        .select(member.id)
                        .from(member)
                        .leftJoin(member.team, team)
                        .where(
                                usernameEq(condition.getUsername()),
                                teamNameEq(condition.getTeamName()),
                                ageGoe(condition.getAgeGoe()),
                                ageLoe(condition.getAgeLoe())
                        )
        );
        return result;
    }


    private BooleanExpression usernameEq(String usernmae) {
        return isEmpty(usernmae) ? null : member.username.eq(usernmae);
    }

    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe == null ? null : member.age.loe(ageLoe);
    }
}
