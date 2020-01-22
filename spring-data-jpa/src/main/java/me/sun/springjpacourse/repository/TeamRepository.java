package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
