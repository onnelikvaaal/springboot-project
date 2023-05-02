package ru.skypro.lessons.springboot.weblibrary.service;

import org.springframework.stereotype.Service;
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
        /*List<Employee> employees = getAllEmployees();
        int sum = 0;
        for (Employee employee : employees) {
           sum += employee.getSalary();
        }
        return sum;*/
        return getAllEmployees().stream().mapToInt(Employee::getSalary).sum();
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

        /*int minSalary = getAllEmployees().stream().mapToInt(Employee::getSalary).min().getAsInt();
        return getAllEmployees().stream().filter(e -> e.getSalary() == minSalary).findFirst().get();*/
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
        /*List<Employee> resultList = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getSalary() > avgSalary) {
                resultList.add(employee);
            }
        }
        return resultList;*/

        return employees.stream().filter(e -> e.getSalary() > avgSalary).collect(Collectors.toList());
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.getAllEmployees();
    }

}
