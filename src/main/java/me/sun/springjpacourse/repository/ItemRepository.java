package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
