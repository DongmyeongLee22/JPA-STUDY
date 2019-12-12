package me.sun.springjpacourse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing// (modifyOnCreate = false) 이러면 업데이트는 null이 들어가나 사용 권장안함.
@SpringBootApplication
public class SpringJpaCourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringJpaCourseApplication.class, args);
    }


    /*
    인터페이스에서 메소드 하나면 람다로 변경 가능
    실제에서는 UUID로 하는게 아니라 스프링 시큐리티 컨텍스트 홀더나 그런데서
    세션 정보를 가져와서 그 사람의 아이디를 꺼내야 한다.

    스프링 데이터 jpa가 등록이나 수정될때 마다 얘를 호출해서 이 결과물을 꺼내간다. 그리고 값을 채운다.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(UUID.randomUUID().toString());
    }

}
