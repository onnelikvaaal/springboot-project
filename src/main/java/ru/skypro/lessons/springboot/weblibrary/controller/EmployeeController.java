package ru.skypro.lessons.springboot.weblibrary.controller;

import org.springframework.web.bind.annotation.*;
import ru.skypro.lessons.springboot.weblibrary.dto.EmployeeDTO;
import ru.skypro.lessons.springboot.weblibrary.exceptions.EmployeeNotFoundException;
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
    public EmployeeDTO getMinSalaryEmployee() {
        return employeeService.getMinSalaryEmployee();
    }

    @GetMapping("/salary/max")
    public EmployeeDTO getMaxSalaryEmployee() {
        return employeeService.getMaxSalaryEmployee();
    }

    @GetMapping("/high-salary")
    public List<EmployeeDTO> getAllAboveAverageSalary() {
        return employeeService.getAllAboveAverageSalary();
    }

    @GetMapping("/get-all")
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getEmployees();
    }

    @GetMapping("/{id}")
    public EmployeeDTO getEmployeeById(@PathVariable int id) throws EmployeeNotFoundException {
        return employeeService.getEmployeeById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployeeById(@PathVariable int id) throws EmployeeNotFoundException {
        employeeService.deleteEmployeeById(id);
    }

    @GetMapping("/salaryHigherThan")
    public List<EmployeeDTO> getEmployeesWithSalaryHigherThan(@RequestParam("salary") int salary) {
        return employeeService.getEmployeesWithSalaryHigherThan(salary);
    }

    @PostMapping("/")
    void createEmployees(@RequestBody List<EmployeeDTO> employeeDTOs) {
        employeeService.createEmployees(employeeDTOs);
    }

    @PutMapping("/{id}")
    void editEmployee(@PathVariable int id,
                      @RequestBody EmployeeDTO employeeDTO) throws EmployeeNotFoundException {
        employeeService.editEmployee(id, employeeDTO);
    }

    @GetMapping("/withHighestSalary")
    public List<EmployeeDTO> getMaxSalaryEmployees() {
        return employeeService.getMaxSalaryEmployees();
    }

    @GetMapping("/")
    public List<EmployeeDTO> getEmployeesByPosition(@RequestParam(name = "position", required = false) String position) {
        return employeeService.getEmployeesByPosition(position);
    }

    @GetMapping("/{id}/fullInfo")
    public EmployeeDTO getFullInfoEmployeeById(@PathVariable int id) throws EmployeeNotFoundException {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/page")
    public List<EmployeeDTO> getEmployeesByPage(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return employeeService.getEmployeesByPage(page, size);
    }
}
