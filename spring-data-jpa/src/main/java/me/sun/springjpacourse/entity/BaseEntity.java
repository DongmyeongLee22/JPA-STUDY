package me.sun.springjpacourse.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/*
스프링 JPA를 이용하면 간단하게 구현가능
 */
@Getter
@EntityListeners(AuditingEntityListener.class) // 이놈을 넣어줘야 구현이 가능하다. xml에 적용해서 자동 설정가능
@MappedSuperclass
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    /*
    시간은 알아서 시간 계산해서 넣으면 되지만 값은 그게 안되므로 추가 설정필요
    MainApplicaiton에 Bean등록해줘야한다.
     */
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    /* 실무에서 사용 예
    등록일, 수정일만 필요할때가 있고
    등록일, 수정일, 등록자, 수정자가 필요할 때가 있다.
    그렇기 때문에 BaseTimeEntity에는 일자를 BaseEntity에는 BaseTimeEntity를 상속 받고 등록자, 수정자를 넣는다.
    work package 참고
     */
}
