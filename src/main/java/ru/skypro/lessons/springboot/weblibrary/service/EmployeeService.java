package ru.skypro.lessons.springboot.weblibrary.service;

import ru.skypro.lessons.springboot.weblibrary.exceptions.EmployeeNotFoundException;
import ru.skypro.lessons.springboot.weblibrary.pojo.Employee;

import java.util.List;

public interface EmployeeService {

    int getEmployeeSalarySum();
    Employee getMinSalaryEmployee();
    Employee getMaxSalaryEmployee();
    List<Employee> getAllAboveAverageSalary();
    List<Employee> getAllEmployees();

    void createEmployees(List<Employee> employees);
    void editEmployee(int id, Employee employee) throws EmployeeNotFoundException;
    Employee getEmployeeById(int id) throws EmployeeNotFoundException;
    void deleteEmployeeById(int id) throws EmployeeNotFoundException;
    List<Employee> getEmployeesWithSalaryHigherThan(int salary);

}
