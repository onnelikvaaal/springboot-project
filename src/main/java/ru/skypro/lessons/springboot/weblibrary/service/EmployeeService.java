package ru.skypro.lessons.springboot.weblibrary.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.lessons.springboot.weblibrary.dto.EmployeeDTO;
import ru.skypro.lessons.springboot.weblibrary.exceptions.EmployeeNotFoundException;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {

    int getEmployeeSalarySum();
    EmployeeDTO getMinSalaryEmployee();
    EmployeeDTO getMaxSalaryEmployee();
    List<EmployeeDTO> getAllAboveAverageSalary();
    List<EmployeeDTO> getEmployees();

    void createEmployees(List<EmployeeDTO> employee);
    void editEmployee(int id, EmployeeDTO employee) throws EmployeeNotFoundException;
    EmployeeDTO getEmployeeById(int id) throws EmployeeNotFoundException;
    void deleteEmployeeById(int id);
    List<EmployeeDTO> getEmployeesWithSalaryHigherThan(int salary);

    List<EmployeeDTO> getMaxSalaryEmployees();

    List<EmployeeDTO> getEmployeesByPosition(String position);

    List<EmployeeDTO> getEmployeesByPage(int pageIndex, int unitPerPage);

    void createEmployeesByFile(MultipartFile file) throws IOException;



}
