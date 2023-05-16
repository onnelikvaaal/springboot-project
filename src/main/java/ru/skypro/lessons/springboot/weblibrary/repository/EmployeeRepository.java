package ru.skypro.lessons.springboot.weblibrary.repository;

import ru.skypro.lessons.springboot.weblibrary.exceptions.EmployeeNotFoundException;
import ru.skypro.lessons.springboot.weblibrary.pojo.Employee;

import java.util.List;

public interface EmployeeRepository {
    List<Employee> getAllEmployees();

    void createEmployees(List<Employee> employees);
    void editEmployee(int id, Employee employee) throws EmployeeNotFoundException;
    Employee getEmployeeById(int id) throws EmployeeNotFoundException;
    void deleteEmployeeById(int id) throws EmployeeNotFoundException;
    List<Employee> getEmployeesWithSalaryHigherThan(int salary);
}

