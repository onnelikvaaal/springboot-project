package ru.skypro.lessons.springboot.weblibrary.controller;

import org.springframework.web.bind.annotation.*;
import ru.skypro.lessons.springboot.weblibrary.exceptions.EmployeeNotFoundException;
import ru.skypro.lessons.springboot.weblibrary.pojo.Employee;
import ru.skypro.lessons.springboot.weblibrary.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/salary/sum")
    public int getEmployeeSalarySum() {
        return employeeService.getEmployeeSalarySum();
    }

    @GetMapping("/salary/min")
    public Employee getMinSalaryEmployee() {
        return employeeService.getMinSalaryEmployee();
    }

    @GetMapping("/salary/max")
    public Employee getMaxSalaryEmployee() {
        return employeeService.getMaxSalaryEmployee();
    }

    @GetMapping("/high-salary")
    public List<Employee> getAllAboveAverageSalary() {
        return employeeService.getAllAboveAverageSalary();
    }

    @GetMapping("/get-all")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable int id) throws EmployeeNotFoundException {
        return employeeService.getEmployeeById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployeeById(@PathVariable int id) throws EmployeeNotFoundException {
        employeeService.deleteEmployeeById(id);
    }

    @GetMapping("/salaryHigherThan")
    public List<Employee> getEmployeesWithSalaryHigherThan(@RequestParam("salary") int salary) {
        return employeeService.getEmployeesWithSalaryHigherThan(salary);
    }

    @PostMapping("/")
    void createEmployees(@RequestBody List<Employee> employees) {
        employeeService.createEmployees(employees);
    }

    @PutMapping("/{id}")
    void editEmployee(@PathVariable int id,
                      @RequestBody Employee employee) throws EmployeeNotFoundException {
        employeeService.editEmployee(id, employee);
    }
}
