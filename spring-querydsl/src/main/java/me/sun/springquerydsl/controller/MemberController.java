package me.sun.springquerydsl.controller;

import lombok.RequiredArgsConstructor;
import me.sun.springquerydsl.MemberSearchCondition;
import me.sun.springquerydsl.MemberTeamDto;
import me.sun.springquerydsl.repository.MemberJPARepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJPARepository memberJPARepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchV1(MemberSearchCondition condition) {
        return memberJPARepository.search(condition);
    }
}
