package ru.skypro.lessons.springboot.weblibrary.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.lessons.springboot.weblibrary.dto.EmployeeDTO;
import ru.skypro.lessons.springboot.weblibrary.exceptions.EmployeeNotFoundException;
import ru.skypro.lessons.springboot.weblibrary.service.EmployeeService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminEmployeeController {

    private final EmployeeService employeeService;


    @DeleteMapping("/{id}")
    public void deleteEmployeeById(@PathVariable int id) {
        employeeService.deleteEmployeeById(id);
    }

    @PostMapping("/create")
    void createEmployees(@RequestBody List<EmployeeDTO> employeeDTOs) {
        employeeService.createEmployees(employeeDTOs);
    }

    @PutMapping("/{id}")
    void editEmployee(@PathVariable int id,
                      @RequestBody EmployeeDTO employeeDTO) throws EmployeeNotFoundException {
        employeeService.editEmployee(id, employeeDTO);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        employeeService.createEmployeesByFile(file);
    }
}