package me.sun.springquerydsl;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
public class MemberQueryProejctDto {
    private String username;
    private int age;

    @QueryProjection
    public MemberQueryProejctDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
