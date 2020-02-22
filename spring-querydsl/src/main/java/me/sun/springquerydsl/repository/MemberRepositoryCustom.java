package me.sun.springquerydsl.repository;

import me.sun.springquerydsl.MemberSearchCondition;
import me.sun.springquerydsl.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Stranger on 2020/02/22
 */
public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCondition condition);
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);
    Page<MemberTeamDto> searhPageComplex(MemberSearchCondition condition, Pageable pageable);
    Page<MemberTeamDto> searhPageComplex2(MemberSearchCondition condition, Pageable pageable);
}
