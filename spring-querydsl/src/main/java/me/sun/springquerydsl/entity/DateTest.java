package me.sun.springquerydsl.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DateTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "date_id")
    private Long id;

    private String name;

    private LocalDate start;

    private LocalDate end;

    private LocalTime time;

    public DateTest(String name, LocalDate start, LocalDate end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }
}
