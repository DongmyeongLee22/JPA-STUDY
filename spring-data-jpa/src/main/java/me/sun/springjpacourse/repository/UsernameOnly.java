package me.sun.springjpacourse.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    /*
        ** 오픈 프로젝션 **
        이렇게 쓰면 Member를 다 가져오고 거기서 분석해서 주는 것
        클로즈 프로젝션은 정해진 값만 가져온다.
     */
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();


    // ** 클로즈 프로젝션 **
    //String getUsername();
}
