package ru.skypro.lessons.springboot.weblibrary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.lessons.springboot.weblibrary.entity.Employee;
import ru.skypro.lessons.springboot.weblibrary.entity.Position;
import ru.skypro.lessons.springboot.weblibrary.entity.Report;
import ru.skypro.lessons.springboot.weblibrary.exceptions.ReportNotFoundException;
import ru.skypro.lessons.springboot.weblibrary.repository.EmployeeRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.PositionRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.ReportRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    private final static String test_position_name = "testPositionName";
    private final static String test_report_data = "testReportData";
    private final static int test_id = 1;

    @Mock
    private EmployeeRepository employeeRepositoryMock;
    @Mock
    private PositionRepository positionRepositoryMock;
    @Mock
    private ReportRepository reportRepositoryMock;

    @InjectMocks
    private ReportServiceImpl out;


    @Test
    public void createReport_saves_report() throws JsonProcessingException {
        Position position = new Position();
        position.setName(test_position_name);
        when(positionRepositoryMock.findAll()).thenReturn(Collections.singletonList(position));
        Employee employee = new Employee();
        employee.setSalary(100);
        when(employeeRepositoryMock.findAllByPosition(eq(position))).thenReturn(Collections.singletonList(employee));
        Report savedReport = new Report();
        savedReport.setId(1);
        when(reportRepositoryMock.save(any())).thenReturn(savedReport);

        out.createReport();

        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepositoryMock).save(captor.capture());
        Report report = captor.getValue();
        String reportData = report.getReportData();

        assertTrue(reportData.contains(test_position_name));
        assertTrue(reportData.contains("\"positionName\":\"testPositionName\""));
        assertTrue(reportData.contains("\"numberOfEmployees\":1"));
        assertTrue(reportData.contains("\"maximumSalary\":100"));
        assertTrue(reportData.contains("\"minimumSalary\":100"));
        assertTrue(reportData.contains("\"averageSalary\":100"));
    }


    @Test
    public void getReportJsonString_returns_report_data_string() throws ReportNotFoundException {
        Report report = new Report();
        report.setReportData(test_report_data);
        when(reportRepositoryMock.findById(eq(test_id))).thenReturn(Optional.of(report));

        String result = out.getReportJsonString(test_id);

        assertEquals(test_report_data, result);
    }
}
