package me.sun.springquerydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class SpringQuerydslApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringQuerydslApplication.class, args);
    }

    /**
     * 싱글톤인데 멀티 스레드에서 문제가 동시성 문제가 없을 까?
     * - JPQQueryFactory의 동시성 문제는 em에 의존한다.
     * - em은 스프링이랑 쓰면 동시성 문제와 관계없이 트랜잭션 단위로 다 따로따로 분리되서 동작한다.
     * - 스프링 em이 진짜 영속성컨텍스트 em이 아닌 프록시로 주입해준다.
     * - 걔가 트랜잭션 단위로 다른데 바인딩 되도록 라우팅해줌
     */
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

}
