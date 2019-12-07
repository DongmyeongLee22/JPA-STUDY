package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.entity.Team;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamRepository {

    @PersistenceContext
    EntityManager em;

    public Team save(Team team) {
        em.persist(team);
        return team;
    }

    public void delete(Team team) {
        em.remove(team);
    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class).getResultList();
    }

    public Optional<Team> findById(Long teamId) {
        Team team = em.find(Team.class, teamId);
        return Optional.ofNullable(team);
    }

    public long count() {
        return em.createQuery("select count(t) from Team t", Long.class).getSingleResult();
    }

    public Team find(Long teamId) {
        return em.find(Team.class, teamId);
    }
}
