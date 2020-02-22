package me.sun.springquerydsl;

import lombok.Data;

/**
 * 회원명, 팀명, 나이를 조회하는 동적쿼리에 사용
 */
@Data
public class MemberSearchCondition {

    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
