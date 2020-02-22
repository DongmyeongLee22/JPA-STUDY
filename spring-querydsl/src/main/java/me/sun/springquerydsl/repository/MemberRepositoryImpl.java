package me.sun.springquerydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.sun.springquerydsl.MemberSearchCondition;
import me.sun.springquerydsl.MemberTeamDto;
import me.sun.springquerydsl.QMemberTeamDto;

import java.util.List;

import static me.sun.springquerydsl.entity.QMember.member;
import static me.sun.springquerydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Created by Stranger on 2020/02/22
 */

/**
 * MemberRepositoryImpl은 규칙이다.
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 만약 검색이 너무 복잡하고 특정 API나 뷰에 특화된 기능이라면 아예 따로 만들어서 사용하는것도 하나의 방법이 될 수있다.
     * 중요한 것은 핵심 비즈니스 로직으로 재사용 가능성이 있는 것들, 혹은 엔티티트를 검색하는 특화된 경우는 MemberRepository에 넣는다.
     * 특정 API에 종속되어 있다면 별도로 MemberQueryRepository를 만들어 사용하는게 나을 수 있다.
     */
    public List<MemberTeamDto> search(MemberSearchCondition condition) {

        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetch();
    }


    // 조합을 위해 BooleanExPression 사용
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
