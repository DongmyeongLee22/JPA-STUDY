package me.sun.springjpacourse.entity;


import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

//원하는 Entity에서 extends하면 된다.
@MappedSuperclass // 속성만 상속하는 Annotation, 진짜 상속은 아니다.
@Getter
public class JpaBaseEntity {

    // DB값이 변화되지 않는다.
    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;


    /* JPA 주요 이벤트 Annotation

    @PrePersist, @PostPersist
    @PreUpdate, @PostUpdate

     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;

        // update에도 now를 넣은 이유는 데이터를 넣어놔야 쿼리날릴 때 편하다
        // 실제 쿼리시 업데이트에 null이 있으면 쿼리하기가 지저분해진다.
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

}
