package ru.skypro.lessons.springboot.weblibrary.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.skypro.lessons.springboot.weblibrary.entity.Employee;
import ru.skypro.lessons.springboot.weblibrary.entity.Position;

import java.util.List;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
    List<Employee> findEmployeesBySalaryGreaterThan(int salary);

    @Query(value = "SELECT * FROM employee ORDER BY salary DESC LIMIT 3",
            nativeQuery = true)
    List<Employee> getMaxSalaryEmployees();

    List<Employee> findAllByPosition(Position position);
}

