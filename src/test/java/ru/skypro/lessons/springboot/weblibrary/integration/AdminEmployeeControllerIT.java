package ru.skypro.lessons.springboot.weblibrary.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.skypro.lessons.springboot.weblibrary.dto.EmployeeDTO;
import ru.skypro.lessons.springboot.weblibrary.entity.Employee;
import ru.skypro.lessons.springboot.weblibrary.entity.Position;
import ru.skypro.lessons.springboot.weblibrary.repository.EmployeeRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.PositionRepository;

import java.util.Collections;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AdminEmployeeControllerIT {

    private static final String TEST_POSITION_NAME = "position";
    private static final String TEST_EMPLOYEE_NAME_1 = "employee1";
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
        employee1.setName(TEST_EMPLOYEE_NAME_1);
        employee1.setSalary(TEST_500);
        employee1.setPosition(savedPosition);
        employeeRepository.save(employee1);
    }

    @AfterEach
    public void afterEach() {
        employeeRepository.deleteAll();
        positionRepository.deleteAll();
    }

    @Test
    void deleteEmployeeById_deletes_employee() throws Exception {
        MvcResult result = mockMvc.perform(get("/employee/get-all")).andReturn();
        Integer employeeId = JsonPath.read(result.getResponse().getContentAsString(), "$[0].id");

        mockMvc.perform(delete("/admin/" + employeeId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/employee/" + employeeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEmployees_creates_new_employee() throws Exception {
        employeeRepository.deleteAll();
        EmployeeDTO employeeDTO = new EmployeeDTO(1, TEST_EMPLOYEE_NAME_1, TEST_500, TEST_POSITION_NAME);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(Collections.singletonList(employeeDTO));

        mockMvc.perform(post("/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(get("/employee/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(TEST_EMPLOYEE_NAME_1))
                .andExpect(jsonPath("$[0].salary").value(TEST_500))
                .andExpect(jsonPath("$[0].position").value(TEST_POSITION_NAME));
    }

    @Test
    void editEmployee_edits_employee() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(1, TEST_EMPLOYEE_NAME_1, TEST_1500, TEST_POSITION_NAME);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(employeeDTO);

        MvcResult result = mockMvc.perform(get("/employee/get-all")).andReturn();
        Integer employeeId = JsonPath.read(result.getResponse().getContentAsString(), "$[0].id");

        mockMvc.perform(put("/admin/" + employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(get("/employee/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(TEST_EMPLOYEE_NAME_1))
                .andExpect(jsonPath("$.salary").value(TEST_1500))
                .andExpect(jsonPath("$.position").value(TEST_POSITION_NAME));
    }

    @Test
    void uploadFile_creates_employee_with_file() throws Exception {
        employeeRepository.deleteAll();
        String fileContent = "[{\n" +
                "        \"name\": \"" + TEST_EMPLOYEE_NAME_1 + "\",\n" +
                "        \"salary\": " + TEST_500 + ",\n" +
                "        \"position\": \"" + TEST_POSITION_NAME + "\"\n" +
                "    }]";
        MockMultipartFile file = new MockMultipartFile("test.json", fileContent.getBytes());

        mockMvc.perform(multipart("/admin/upload").file("file", file.getBytes()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/employee/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(TEST_EMPLOYEE_NAME_1))
                .andExpect(jsonPath("$[0].salary").value(TEST_500))
                .andExpect(jsonPath("$[0].position").value(TEST_POSITION_NAME));
    }
}
