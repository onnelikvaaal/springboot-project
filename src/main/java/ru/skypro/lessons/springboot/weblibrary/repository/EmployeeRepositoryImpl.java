package ru.skypro.lessons.springboot.weblibrary.repository;

import org.springframework.stereotype.Repository;
import ru.skypro.lessons.springboot.weblibrary.pojo.Employee;

import java.util.List;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final List<Employee> employeeList = List.of(
            new Employee("Frodo Baggins", 520),
            new Employee("Samwise Gamgee", 380),
            new Employee("Meriadoc Brendybuck", 550),
            new Employee("Peregrin Took", 650),
            new Employee("Boromir", 640),
            new Employee("Gollum", 320));

    @Override
    public List<Employee> getAllEmployees() {
        return employeeList;
    }
}
