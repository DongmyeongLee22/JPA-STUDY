package me.sun.springjpacourse.repository;

public class UsernameOnlyDTO {

    public final String username;

    // 생성자의 parameter 이름으로 매칭을 시키므로 중요하다.
    public UsernameOnlyDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
