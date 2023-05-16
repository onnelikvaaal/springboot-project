package ru.skypro.lessons.springboot.weblibrary.repository;

import org.springframework.stereotype.Repository;
import ru.skypro.lessons.springboot.weblibrary.exceptions.EmployeeNotFoundException;
import ru.skypro.lessons.springboot.weblibrary.pojo.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private static List<Employee> employeeList = new ArrayList<>();

    //static-блок понадобился для заполнения листа, чтобы имитировать БД
    static {
        employeeList.add(new Employee(1,"Frodo Baggins", 520));
        employeeList.add(new Employee(2,"Samwise Gamgee", 380));
        employeeList.add(new Employee(3,"Meriadoc Brendybuck", 550));
        employeeList.add(new Employee(4,"Peregrin Took", 650));
        employeeList.add(new Employee(5,"Boromir", 640));
        employeeList.add(new Employee(6,"Gollum", 320));
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeList;
    }

    @Override
    public void createEmployees(List<Employee> employees) {
        employeeList.addAll(employees);
    }

    @Override
    public void editEmployee(int id, Employee employee) throws EmployeeNotFoundException {
        Employee employeeToEdit = getEmployeeById(id);
        employeeToEdit.setName(employee.getName());
        employeeToEdit.setSalary(employee.getSalary());
    }

    @Override
    public Employee getEmployeeById(int id) throws EmployeeNotFoundException {
        return employeeList.stream().filter(e -> e.getId() == id)
                .findFirst().orElseThrow(() -> new EmployeeNotFoundException("Employee not found!"));
    }

    @Override
    public void deleteEmployeeById(int id) throws EmployeeNotFoundException {
        Employee employee = getEmployeeById(id);
        employeeList.remove(employee);
    }

    @Override
    public List<Employee> getEmployeesWithSalaryHigherThan(int salary) {
        return employeeList.stream().filter(e -> e.getSalary() > salary).collect(Collectors.toList());
    }
}
