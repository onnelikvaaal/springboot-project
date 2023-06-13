package ru.skypro.lessons.springboot.weblibrary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Get employees salary sum method was invoked");
        return getAllEmployees().stream()
                .mapToInt(Employee::getSalary)
                .sum();
    }

    @Override
    public EmployeeDTO getMinSalaryEmployee() {
        log.info("Get minimum salary employee method was invoked");
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
        log.info("Get maximum salary employee method was invoked");
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
        log.info("Get all above average salary method was invoked");
        List<Employee> employees = getAllEmployees();
        int avgSalary = employees.stream().mapToInt(Employee::getSalary).sum() / employees.size();
        return employees.stream()
                .filter(e -> e.getSalary() > avgSalary)
                .map(employeeTransformer::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployees() {
        log.info("Get employees method was invoked");
        return getAllEmployees().stream()
                .map(employeeTransformer::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    private List<Employee> getAllEmployees() {//приватный метод для получения списка энтитей внутри других методов
        List<Employee> employees = (List<Employee>) employeeRepository.findAll();//через приведение
        log.debug("Successfully found {} employee records from DB", employees.size());
        return employees;
    }

    @Override
    public void createEmployees(List<EmployeeDTO> employeeDTOs) {
        log.info("Create employees method was invoked");
        List<Employee> employeeList = new ArrayList<>();
        for (EmployeeDTO employeeDTO : employeeDTOs) {
            Position position = positionRepository.findByName(employeeDTO.getPosition());
            Employee employee = employeeTransformer.toEmployee(employeeDTO, position);
            employeeList.add(employee);
        }
        employeeRepository.saveAll(employeeList);
        log.debug("Successfully saved employees to DB");
    }

    @Override
    public void editEmployee(int id, EmployeeDTO employeeDTO) throws EmployeeNotFoundException {
        log.info("Edit employee method was invoked");
        try {
            Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found!"));
            employee.setName(employeeDTO.getName());
            employee.setSalary(employeeDTO.getSalary());
            Position position = positionRepository.findByName(employeeDTO.getPosition());
            employee.setPosition(position);
            employeeRepository.save(employee);
            log.debug("Successfully saved employee to DB");
        } catch (EmployeeNotFoundException e) {
            log.error("There is no employee with id = " + id, e);
            throw e;
        }
    }

    @Override
    public EmployeeDTO getEmployeeById(int id) throws EmployeeNotFoundException {
        log.info("Get employee by id method was invoked");
        try {
            EmployeeDTO employeeDTO = employeeRepository.findById(id)
                    .map(employeeTransformer::toEmployeeDTO)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found!"));
            log.debug("Successfully found employee with id = {}", id);
            return employeeDTO;
        } catch (EmployeeNotFoundException e) {
            log.error("There is no employee with id = " + id, e);
            throw e;
        }
    }

    @Override
    public void deleteEmployeeById(int id) {
        log.info("Delete employee by id method was invoked");
        employeeRepository.deleteById(id);
        log.debug("Successfully deleted employee with id = {}", id);
    }

    @Override
    public List<EmployeeDTO> getEmployeesWithSalaryHigherThan(int salary) {
        log.info("Get employees with salary higher than method was invoked");
        List<EmployeeDTO> employeeDTOList = employeeRepository.findEmployeesBySalaryGreaterThan(salary).stream()
                .map(employeeTransformer::toEmployeeDTO)
                .collect(Collectors.toList());
        log.debug("Successfully found employees with salary higher than {}", salary);
        return employeeDTOList;
    }

    @Override
    public List<EmployeeDTO> getMaxSalaryEmployees() {
        log.info("Get maximum salary employees method was invoked");
       List<EmployeeDTO> employeeDTOList = employeeRepository.getMaxSalaryEmployees().stream()
               .map(employeeTransformer::toEmployeeDTO)
               .collect(Collectors.toList());
       log.debug("Successfully found maximum salary employees");
       return employeeDTOList;
    }

    @Override
    public List<EmployeeDTO> getEmployeesByPosition(String position) {
        log.info("Get employees by position method was invoked");
        if (position == null || position.isBlank()) {
            return getEmployees();
        }
        Position positionEntity = positionRepository.findByName(position);
        log.debug("Successfully found {} position in repository", position);
        List<EmployeeDTO> employeeDTOList = employeeRepository.findAllByPosition(positionEntity)
                .stream()
                .map(employeeTransformer::toEmployeeDTO)
                .collect(Collectors.toList());
        log.debug("Successfully found employees by {} position", position);
        return employeeDTOList;
    }

    @Override
    public List<EmployeeDTO> getEmployeesByPage(int pageIndex, int unitPerPage) {
        log.info("Get employees by page method was invokrd");
        PageRequest page = PageRequest.of(pageIndex, unitPerPage);
        Page<Employee> employeePage = employeePagingRepository.findAll(page);
        log.debug("Successfully found employees on page = {}", pageIndex);
        return employeePage.stream()
                .map(employeeTransformer::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createEmployeesByFile(MultipartFile file) throws IOException {
        log.info("Create employees by file method was invoked");
        try {
            String fileString = new String(file.getBytes(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();

            CollectionType javaType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, EmployeeDTO.class);//конструкция для чтения коллекции из json

            List<EmployeeDTO> employeeDTOList = objectMapper.readValue(fileString, javaType);
            createEmployees(employeeDTOList);
        } catch (IOException e) {
            log.error("IOException when parsing employees JSON", e);
            throw e;
        }
    }
}
