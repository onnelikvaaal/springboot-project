package ru.skypro.lessons.springboot.weblibrary.service;

import org.springframework.stereotype.Service;
import ru.skypro.lessons.springboot.weblibrary.exceptions.EmployeeNotFoundException;
import ru.skypro.lessons.springboot.weblibrary.pojo.Employee;
import ru.skypro.lessons.springboot.weblibrary.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public int getEmployeeSalarySum() {
        return getAllEmployees().stream()
                .mapToInt(Employee::getSalary)
                .sum();
    }

    @Override
    public Employee getMinSalaryEmployee() {
        List<Employee> employees = getAllEmployees();
        int min = Integer.MAX_VALUE;
        for (Employee employee : employees) {
            if (employee.getSalary() < min) {
                min = employee.getSalary();
            }
        }
        Employee minEmployee = null;
        for (Employee employee : employees) {
            if (min == employee.getSalary()) {
                minEmployee = employee;
            }
        }
        return minEmployee;
    }

    @Override
    public Employee getMaxSalaryEmployee() {
        List<Employee> employees = getAllEmployees();
        int max = 0;
        for (Employee employee : employees) {
            if (employee.getSalary() > max) {
                max = employee.getSalary();
            }
        }
        Employee maxEmployee = null;
        for (Employee employee : employees) {
            if (max == employee.getSalary()) {
                maxEmployee = employee;
            }
        }
        return maxEmployee;
    }

    @Override
    public List<Employee> getAllAboveAverageSalary() {
        List<Employee> employees = getAllEmployees();
        int avgSalary = employees.stream().mapToInt(Employee::getSalary).sum() / employees.size();
        return employees.stream()
                .filter(e -> e.getSalary() > avgSalary)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.getAllEmployees();
    }

    @Override
    public void createEmployees(List<Employee> employees) {
        employeeRepository.createEmployees(employees);
    }

    @Override
    public void editEmployee(int id, Employee employee) throws EmployeeNotFoundException {
        employeeRepository.editEmployee(id, employee);
    }

    @Override
    public Employee getEmployeeById(int id) throws EmployeeNotFoundException {
        return employeeRepository.getEmployeeById(id);
    }

    @Override
    public void deleteEmployeeById(int id) throws EmployeeNotFoundException {
        employeeRepository.deleteEmployeeById(id);
    }

    @Override
    public List<Employee> getEmployeesWithSalaryHigherThan(int salary) {
        return employeeRepository.getEmployeesWithSalaryHigherThan(salary);
    }
}
