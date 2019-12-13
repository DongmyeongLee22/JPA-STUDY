package me.sun.springjpacourse.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return null;
    }

    /*  @GeneratedValue를 사용할 여건이 안될 때
        Persistable을 사용하면 SimpleJpaReposity의 save 로직에 있는 isNew를 커스텀 할 수 있다.
     */
    @Override
    public boolean isNew() {
        //createdDate를 이용하여 isNew를 확인한다.
        return createdDate == null;
    }
}
