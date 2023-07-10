package ru.skypro.lessons.springboot.weblibrary.integration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.skypro.lessons.springboot.weblibrary.entity.Employee;
import ru.skypro.lessons.springboot.weblibrary.entity.Position;
import ru.skypro.lessons.springboot.weblibrary.repository.EmployeeRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.PositionRepository;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeControllerIT {

    private static final String TEST_POSITION_NAME = "position";
    private static final String TEST_EMPLOYEE_NAME_1 = "employee1";
    private static final String TEST_EMPLOYEE_NAME_2 = "employee2";
    private static final String TEST_EMPLOYEE_NAME_3 = "employee3";

    private static final int TEST_1000 = 1000;
    private static final int TEST_500 = 500;
    private static final int TEST_1500 = 1500;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PositionRepository positionRepository;

    @BeforeEach
    public void beforeEach() {
        Position position = new Position();
        position.setName(TEST_POSITION_NAME);
        Position savedPosition = positionRepository.save(position);
        Employee employee1 = new Employee();
        employee1.setId(1);
        employee1.setName(TEST_EMPLOYEE_NAME_1);
        employee1.setSalary(TEST_500);
        employee1.setPosition(savedPosition);
        Employee employee2 = new Employee();
        employee2.setName(TEST_EMPLOYEE_NAME_2);
        employee2.setSalary(TEST_1000);
        employee2.setPosition(savedPosition);
        Employee employee3 = new Employee();
        employee3.setName(TEST_EMPLOYEE_NAME_3);
        employee3.setSalary(TEST_1500);
        employee3.setPosition(savedPosition);
        employeeRepository.saveAll(Arrays.asList(employee1, employee2, employee3));
    }

    @AfterEach
    public void afterEach() {
        employeeRepository.deleteAll();
        positionRepository.deleteAll();
    }

    @Test
    void getEmployeeSalarySum_returns_salary_sum() throws Exception {
        mockMvc.perform(get("/employee/salary/sum"))
                .andExpect(status().isOk())
                .andExpect(content().string("3000"));
    }

    @Test
    void getMinSalaryEmployee_returns_correct_employee() throws Exception {
        mockMvc.perform(get("/employee/salary/min"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(TEST_EMPLOYEE_NAME_1))
                .andExpect(jsonPath("$.salary").value(TEST_500))
                .andExpect(jsonPath("$.position").value(TEST_POSITION_NAME));
    }

    @Test
    void getMaxSalaryEmployee_returns_correct_employee() throws Exception {
        mockMvc.perform(get("/employee/salary/max"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(TEST_EMPLOYEE_NAME_3))
                .andExpect(jsonPath("$.salary").value(TEST_1500))
                .andExpect(jsonPath("$.position").value(TEST_POSITION_NAME));
    }


    @Test
    void getAllAboveAverageSalary_returns_correct_list_of_employees() throws Exception {
        mockMvc.perform(get("/employee/high-salary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(TEST_EMPLOYEE_NAME_3))
                .andExpect(jsonPath("$[0].salary").value(TEST_1500))
                .andExpect(jsonPath("$[0].position").value(TEST_POSITION_NAME));
    }

    @Test
    void getAllEmployees_returns_list_of_employees() throws Exception {
        mockMvc.perform(get("/employee/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(TEST_EMPLOYEE_NAME_1))
                .andExpect(jsonPath("$[0].salary").value(TEST_500))
                .andExpect(jsonPath("$[0].position").value(TEST_POSITION_NAME))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].name").value(TEST_EMPLOYEE_NAME_2))
                .andExpect(jsonPath("$[1].salary").value(TEST_1000))
                .andExpect(jsonPath("$[1].position").value(TEST_POSITION_NAME))
                .andExpect(jsonPath("$[2].id").isNumber())
                .andExpect(jsonPath("$[2].name").value(TEST_EMPLOYEE_NAME_3))
                .andExpect(jsonPath("$[2].salary").value(TEST_1500))
                .andExpect(jsonPath("$[2].position").value(TEST_POSITION_NAME));
    }

    @Test
    void getEmployeeById_returns_correct_employee() throws Exception {
        MvcResult result = mockMvc.perform(get("/employee/get-all")).andReturn();
        Integer employeeId = JsonPath.read(result.getResponse().getContentAsString(), "$[0].id");
        mockMvc.perform(get("/employee/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(TEST_EMPLOYEE_NAME_1))
                .andExpect(jsonPath("$.salary").value(TEST_500))
                .andExpect(jsonPath("$.position").value(TEST_POSITION_NAME));
    }

    @Test
    void getEmployeeById_returns_error_when_employee_not_found() throws Exception {
        mockMvc.perform(get("/employee/100500"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee not found!"));
    }

    @Test
    void getEmployeesWithSalaryHigherThan_returns_correct_list_of_employees() throws Exception {
        mockMvc.perform(get("/employee/salaryHigherThan").param("salary", "1200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(TEST_EMPLOYEE_NAME_3))
                .andExpect(jsonPath("$[0].salary").value(TEST_1500))
                .andExpect(jsonPath("$[0].position").value(TEST_POSITION_NAME));
    }

    @Test
    void getMaxSalaryEmployees_returns_correct_list_of_employees() throws Exception {
        mockMvc.perform(get("/employee/withHighestSalary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(TEST_EMPLOYEE_NAME_3))
                .andExpect(jsonPath("$[0].salary").value(TEST_1500))
                .andExpect(jsonPath("$[0].position").value(TEST_POSITION_NAME))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].name").value(TEST_EMPLOYEE_NAME_2))
                .andExpect(jsonPath("$[1].salary").value(TEST_1000))
                .andExpect(jsonPath("$[1].position").value(TEST_POSITION_NAME))
                .andExpect(jsonPath("$[2].id").isNumber())
                .andExpect(jsonPath("$[2].name").value(TEST_EMPLOYEE_NAME_1))
                .andExpect(jsonPath("$[2].salary").value(TEST_500))
                .andExpect(jsonPath("$[2].position").value(TEST_POSITION_NAME));
    }

    @Test
    void getEmployeesByPosition_returns_correct_list_of_employees() throws Exception {
        mockMvc.perform(get("/employee/").param("position", TEST_POSITION_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(TEST_EMPLOYEE_NAME_1))
                .andExpect(jsonPath("$[0].salary").value(TEST_500))
                .andExpect(jsonPath("$[0].position").value(TEST_POSITION_NAME))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].name").value(TEST_EMPLOYEE_NAME_2))
                .andExpect(jsonPath("$[1].salary").value(TEST_1000))
                .andExpect(jsonPath("$[1].position").value(TEST_POSITION_NAME))
                .andExpect(jsonPath("$[2].id").isNumber())
                .andExpect(jsonPath("$[2].name").value(TEST_EMPLOYEE_NAME_3))
                .andExpect(jsonPath("$[2].salary").value(TEST_1500))
                .andExpect(jsonPath("$[2].position").value(TEST_POSITION_NAME));
    }

    @Test
    void getFullInfoEmployeeById_returns_correct_employee() throws Exception {
        MvcResult result = mockMvc.perform(get("/employee/get-all")).andReturn();
        Integer employeeId = JsonPath.read(result.getResponse().getContentAsString(), "$[0].id");
        mockMvc.perform(get("/employee/" + employeeId + "/fullInfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(TEST_EMPLOYEE_NAME_1))
                .andExpect(jsonPath("$.salary").value(TEST_500))
                .andExpect(jsonPath("$.position").value(TEST_POSITION_NAME));
    }

    @Test
    void getEmployeesByPage_returns_correct_list_of_employees() throws Exception {
        mockMvc.perform(get("/employee/page").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(TEST_EMPLOYEE_NAME_1))
                .andExpect(jsonPath("$[0].salary").value(TEST_500))
                .andExpect(jsonPath("$[0].position").value(TEST_POSITION_NAME))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].name").value(TEST_EMPLOYEE_NAME_2))
                .andExpect(jsonPath("$[1].salary").value(TEST_1000))
                .andExpect(jsonPath("$[1].position").value(TEST_POSITION_NAME));
    }
}
