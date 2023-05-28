package ru.skypro.lessons.springboot.weblibrary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.lessons.springboot.weblibrary.dto.EmployeeDTO;
import ru.skypro.lessons.springboot.weblibrary.entity.Employee;
import ru.skypro.lessons.springboot.weblibrary.entity.Position;
import ru.skypro.lessons.springboot.weblibrary.exceptions.EmployeeNotFoundException;
import ru.skypro.lessons.springboot.weblibrary.repository.EmployeePagingRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.EmployeeRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.PositionRepository;
import ru.skypro.lessons.springboot.weblibrary.transformer.EmployeeTransformer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final EmployeePagingRepository employeePagingRepository;
    private final EmployeeTransformer employeeTransformer;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               PositionRepository positionRepository,
                               EmployeePagingRepository employeePagingRepository,
                               EmployeeTransformer employeeTransformer) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
        this.employeePagingRepository = employeePagingRepository;
        this.employeeTransformer = employeeTransformer;
    }

    @Override
    public int getEmployeeSalarySum() {
        return getAllEmployees().stream()
                .mapToInt(Employee::getSalary)
                .sum();
    }

    @Override
    public EmployeeDTO getMinSalaryEmployee() {
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
        return employeeTransformer.toEmployeeDTO(minEmployee);
    }

    @Override
    public EmployeeDTO getMaxSalaryEmployee() {
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
        return employeeTransformer.toEmployeeDTO(maxEmployee);
    }

    @Override
    public List<EmployeeDTO> getAllAboveAverageSalary() {
        List<Employee> employees = getAllEmployees();
        int avgSalary = employees.stream().mapToInt(Employee::getSalary).sum() / employees.size();
        return employees.stream()
                .filter(e -> e.getSalary() > avgSalary)
                .map(employeeTransformer::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployees() {
        return getAllEmployees().stream()
                .map(employeeTransformer::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    private List<Employee> getAllEmployees() {//приватный метод для получения списка энтитей внутри других методов
        return (List<Employee>) employeeRepository.findAll();//через приведение
    }

    @Override
    public void createEmployees(List<EmployeeDTO> employeeDTOs) {
        List<Employee> employeeList = new ArrayList<>();
        for (EmployeeDTO employeeDTO : employeeDTOs) {
            Position position = positionRepository.findByName(employeeDTO.getPosition());
            Employee employee = employeeTransformer.toEmployee(employeeDTO, position);
            employeeList.add(employee);
        }
        employeeRepository.saveAll(employeeList);
    }

    @Override
    public void editEmployee(int id, EmployeeDTO employeeDTO) throws EmployeeNotFoundException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found!"));
        employee.setName(employeeDTO.getName());
        employee.setSalary(employeeDTO.getSalary());
        Position position = positionRepository.findByName(employeeDTO.getPosition());
        employee.setPosition(position);
        employeeRepository.save(employee);
    }

    @Override
    public EmployeeDTO getEmployeeById(int id) throws EmployeeNotFoundException {
        return employeeRepository.findById(id)
                .map(employeeTransformer::toEmployeeDTO)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found!"));
    }

    @Override
    public void deleteEmployeeById(int id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithSalaryHigherThan(int salary) {
        return employeeRepository.findEmployeesBySalaryGreaterThan(salary).stream()
                .map(employeeTransformer::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getMaxSalaryEmployees() {
       return employeeRepository.getMaxSalaryEmployees().stream()
               .map(employeeTransformer::toEmployeeDTO)
               .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesByPosition(String position) {
        if (position == null || position.isBlank()) {
            return getEmployees();
        }
        Position positionEntity = positionRepository.findByName(position);
        return employeeRepository.findAllByPosition(positionEntity).stream()
                .map(employeeTransformer::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesByPage(int pageIndex, int unitPerPage) {
        PageRequest page = PageRequest.of(pageIndex, unitPerPage);
        Page<Employee> employeePage = employeePagingRepository.findAll(page);
        return employeePage.stream()
                .map(employeeTransformer::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createEmployeesByFile(MultipartFile file) throws IOException {
        String fileString = new String(file.getBytes(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        CollectionType javaType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, EmployeeDTO.class);//конструкция для чтения коллекции из json

        List<EmployeeDTO> employeeDTOList = objectMapper.readValue(fileString, javaType);
        createEmployees(employeeDTOList);
    }
}
