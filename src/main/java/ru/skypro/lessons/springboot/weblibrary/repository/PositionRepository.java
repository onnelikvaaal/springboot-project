package ru.skypro.lessons.springboot.weblibrary.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skypro.lessons.springboot.weblibrary.entity.Position;

public interface PositionRepository extends CrudRepository<Position, Integer> {

    Position findByName(String name);
}
