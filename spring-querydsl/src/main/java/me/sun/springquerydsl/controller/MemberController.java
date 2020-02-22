package me.sun.springquerydsl.controller;

import lombok.RequiredArgsConstructor;
import me.sun.springquerydsl.MemberSearchCondition;
import me.sun.springquerydsl.MemberTeamDto;
import me.sun.springquerydsl.repository.MemberJPARepository;
import me.sun.springquerydsl.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJPARepository memberJPARepository;
    private final MemberRepository memberRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchV1(MemberSearchCondition condition) {
        return memberJPARepository.search(condition);
    }

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> serachV2(MemberSearchCondition condition, Pageable pageable){
        return memberRepository.searchPageSimple(condition, pageable);
    }
}
