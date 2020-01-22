package me.sun.springjpacourse.dto;

import lombok.Data;
import me.sun.springjpacourse.entity.Member;

@Data
public class MemberDto {

    private Long id;

    private String username;

    private String teamname;

    public MemberDto(Long id, String username, String teamname) {
        this.id = id;
        this.username = username;
        this.teamname = teamname;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.username = member.getTeam().getName();
        ;
    }
}
