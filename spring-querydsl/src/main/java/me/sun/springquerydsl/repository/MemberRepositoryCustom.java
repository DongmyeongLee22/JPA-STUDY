package me.sun.springquerydsl.repository;

import me.sun.springquerydsl.MemberSearchCondition;
import me.sun.springquerydsl.MemberTeamDto;

import java.util.List;

/**
 * Created by Stranger on 2020/02/22
 */
public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCondition condition);
}
