package ru.skypro.lessons.springboot.weblibrary.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
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
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    private final static int test_employee_id = 2;
    private final static String test_position_name = "testPositionName";
    private final static String test_name = "testName";
    private final static int test_salary = 150;

    @Mock//сделай имитацию-заглушку этого объекта и положи в это поле
    private EmployeeRepository employeeRepositoryMock;
    @Mock
    private PositionRepository positionRepositoryMock;
    @Mock
    private EmployeePagingRepository employeePagingRepositoryMock;
    @Mock
    private EmployeeTransformer employeeTransformerMock;

    @InjectMocks//все моки положи в этот объект
    private EmployeeServiceImpl out;

    @Test
    public void getEmployeeSalarySum_returns_correct_sum() {
        Employee employee1 = new Employee();
        employee1.setSalary(100);
        Employee employee2 = new Employee();
        employee2.setSalary(200);
        when(employeeRepositoryMock.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        int result = out.getEmployeeSalarySum();

        assertEquals(300, result);
    }

    @Test
    public void getMinSalaryEmployee_returns_correct_employee() {
        Employee employee1 = new Employee();
        employee1.setSalary(100);
        Employee employee2 = new Employee();
        employee2.setSalary(200);
        when(employeeRepositoryMock.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        EmployeeDTO dto1 = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee1))).thenReturn(dto1);

        EmployeeDTO result = out.getMinSalaryEmployee();

        assertEquals(dto1, result);
    }

    @Test
    public void getMinSalaryEmployee_returns_null_when_no_employees_found() {
        when(employeeRepositoryMock.findAll()).thenReturn(Collections.EMPTY_LIST);
        when(employeeTransformerMock.toEmployeeDTO(eq(null))).thenReturn(null);

        EmployeeDTO result = out.getMinSalaryEmployee();
        assertNull(result);
    }

    @Test
    public void getMaxSalaryEmployee_returns_correct_employee() {
        Employee employee1 = new Employee();
        employee1.setSalary(100);
        Employee employee2 = new Employee();
        employee2.setSalary(200);
        when(employeeRepositoryMock.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        EmployeeDTO dto = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee2))).thenReturn(dto);

        EmployeeDTO result = out.getMaxSalaryEmployee();

        assertEquals(dto, result);
    }

    @Test
    public void getAllAboveAverageSalary_returns_correct_list_of_employees() {
        Employee employee1 = new Employee();
        employee1.setSalary(100);
        Employee employee2 = new Employee();
        employee2.setSalary(200);
        when(employeeRepositoryMock.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        EmployeeDTO dto = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee2))).thenReturn(dto);

        List<EmployeeDTO> resultList = out.getAllAboveAverageSalary();

        assertEquals(dto, resultList.get(0));
    }

    @Test
    public void getEmployees_returns_correct_list_of_employees() {
        Employee employee1 = new Employee();
        employee1.setSalary(100);
        Employee employee2 = new Employee();
        employee2.setSalary(200);
        when(employeeRepositoryMock.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        EmployeeDTO dto1 = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee1))).thenReturn(dto1);
        EmployeeDTO dto2 = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee2))).thenReturn(dto2);

        List<EmployeeDTO> resultList = out.getEmployees();

        assertTrue(resultList.contains(dto1));
        assertTrue(resultList.contains(dto2));
    }

    @Test
    public void createEmployees_saves_employees() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        String positionName = "testPosition";
        employeeDTO.setPosition(positionName);
        Position position = new Position();
        Employee employee = new Employee();

        when(positionRepositoryMock.findByName(eq(positionName))).thenReturn(position);
        when(employeeTransformerMock.toEmployee(eq(employeeDTO), eq(position))).thenReturn(employee);

        out.createEmployees(Collections.singletonList(employeeDTO));

        ArgumentCaptor<List<Employee>> captor = ArgumentCaptor.forClass(List.class);
        verify(employeeRepositoryMock, times(1)).saveAll(captor.capture());
        List<Employee> savedList = captor.getValue();

        assertTrue(savedList.contains(employee));
    }

    @Test
    public void editEmployee_edits_employee() throws EmployeeNotFoundException {
        Employee employee = new Employee();
        when(employeeRepositoryMock.findById(eq(test_employee_id))).thenReturn(Optional.of(employee));
        Position position = new Position();
        when(positionRepositoryMock.findByName(eq(test_position_name))).thenReturn(position);
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setName(test_name);
        employeeDTO.setSalary(test_salary);
        employeeDTO.setPosition(test_position_name);

        out.editEmployee(test_employee_id, employeeDTO);

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepositoryMock, times(1)).save(captor.capture());
        Employee savedEmployee = captor.getValue();

        assertNotNull(savedEmployee);
        assertEquals(test_name, savedEmployee.getName());
        assertEquals(test_salary, savedEmployee.getSalary());
        assertEquals(position, savedEmployee.getPosition());
    }

    @Test
    public void editEmployee_throws_exception_when_employee_not_found() {
        when(employeeRepositoryMock.findById(eq(test_employee_id))).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> out.editEmployee(test_employee_id, new EmployeeDTO()));
    }

    @Test
    public void getEmployeeById_returns_correct_employee() throws EmployeeNotFoundException {
        Employee employee = new Employee();
        when(employeeRepositoryMock.findById(eq(test_employee_id))).thenReturn(Optional.of(employee));

        EmployeeDTO dto = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee))).thenReturn(dto);

        EmployeeDTO result = out.getEmployeeById(test_employee_id);
        assertEquals(dto, result);
    }

    @Test
    public void deleteEmployeeById_deletes_employee() {
        out.deleteEmployeeById(test_employee_id);

        verify(employeeRepositoryMock, times(1)).deleteById(eq(test_employee_id));
    }

    @Test
    public void getEmployeesWithSalaryHigherThan_returns_correct_list_of_employees() {
        Employee employee1 = new Employee();
        employee1.setSalary(100);
        Employee employee2 = new Employee();
        employee2.setSalary(200);
        when(employeeRepositoryMock.findEmployeesBySalaryGreaterThan(eq(test_salary))).thenReturn(Arrays.asList(employee1, employee2));
        EmployeeDTO dto1 = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee1))).thenReturn(dto1);
        EmployeeDTO dto2 = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee2))).thenReturn(dto2);

        List<EmployeeDTO> resultList = out.getEmployeesWithSalaryHigherThan(test_salary);
        assertTrue(resultList.contains(dto1));
        assertTrue(resultList.contains(dto2));
    }

    @Test
    public void getEmployeesByPosition_returns_correct_list_of_employees() {
        Position position = new Position();
        when(positionRepositoryMock.findByName(eq(test_position_name))).thenReturn(position);
        Employee employee = new Employee();
        when(employeeRepositoryMock.findAllByPosition(eq(position))).thenReturn(Collections.singletonList(employee));
        EmployeeDTO employeeDTO = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee))).thenReturn(employeeDTO);

        List<EmployeeDTO> resultList = out.getEmployeesByPosition(test_position_name);
        assertTrue(resultList.contains(employeeDTO));
    }

    @ParameterizedTest
    @MethodSource("getEmployeesByPosition_returns_all_employees_when_position_name_is_null_or_blank_params")
    public void getEmployeesByPosition_returns_all_employees_when_position_name_is_null_or_blank(String positionName) {
        Employee employee1 = new Employee();
        employee1.setSalary(100);
        Employee employee2 = new Employee();
        employee2.setSalary(200);
        when(employeeRepositoryMock.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        EmployeeDTO dto1 = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee1))).thenReturn(dto1);
        EmployeeDTO dto2 = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee2))).thenReturn(dto2);

        List<EmployeeDTO> resultList = out.getEmployeesByPosition(positionName);

        assertTrue(resultList.contains(dto1));
        assertTrue(resultList.contains(dto2));
    }

    public static Stream<Arguments> getEmployeesByPosition_returns_all_employees_when_position_name_is_null_or_blank_params() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(""),
                Arguments.of(" ")
        );
    }

    @Test
    public void getEmployeesByPage_returns_correct_list_of_employees() {
        Employee employee = new Employee();
        Page<Employee> page = new PageImpl(Collections.singletonList(employee));
        when(employeePagingRepositoryMock.findAll(any(Pageable.class))).thenReturn(page);
        EmployeeDTO dto = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee))).thenReturn(dto);

        List<EmployeeDTO> resultList = out.getEmployeesByPage(1, 10);
        assertTrue(resultList.contains(dto), "result list should contain employee dto");

        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(employeePagingRepositoryMock, times(1)).findAll(captor.capture());
        PageRequest pageRequest = captor.getValue();

        assertEquals(1, pageRequest.getPageNumber(), "page number should be 1");
        assertEquals(10, pageRequest.getPageSize(), "page size should be 10");
    }

    @Test
    public void getMaxSalaryEmployees_returns_correct_list_of_employees() {
        Employee employee = new Employee();
        when(employeeRepositoryMock.getMaxSalaryEmployees()).thenReturn(Collections.singletonList(employee));
        EmployeeDTO employeeDTO = new EmployeeDTO();
        when(employeeTransformerMock.toEmployeeDTO(eq(employee))).thenReturn(employeeDTO);

        List<EmployeeDTO> resultList = out.getMaxSalaryEmployees();
        assertTrue(resultList.contains(employeeDTO));
    }

    @Test
    public void createEmployeesByFile_creates_employees() throws IOException {
        String fileContent = "[{\n" +
                "        \"name\": \"" + test_name + "\",\n" +
                "        \"salary\": " + test_salary + ",\n" +
                "        \"position\": \"" + test_position_name + "\"\n" +
                "    }]";
        MultipartFile file = new MockMultipartFile("Filename", fileContent.getBytes());

        Position position = new Position();
        when(positionRepositoryMock.findByName(eq(test_position_name))).thenReturn(position);
        Employee employee = new Employee();
        when(employeeTransformerMock.toEmployee(any(), eq(position))).thenReturn(employee);

        out.createEmployeesByFile(file);

        ArgumentCaptor<List<Employee>> captor = ArgumentCaptor.forClass(List.class);
        verify(employeeRepositoryMock, times(1)).saveAll(captor.capture());
        List<Employee> savedList = captor.getValue();
        assertTrue(savedList.contains(employee));

        ArgumentCaptor<EmployeeDTO> dtoCaptor = ArgumentCaptor.forClass(EmployeeDTO.class);
        verify(employeeTransformerMock).toEmployee(dtoCaptor.capture(), any());
        EmployeeDTO dto = dtoCaptor.getValue();
        assertEquals(test_name, dto.getName());
        assertEquals(test_salary, dto.getSalary());
        assertEquals(test_position_name, dto.getPosition());
    }
}
