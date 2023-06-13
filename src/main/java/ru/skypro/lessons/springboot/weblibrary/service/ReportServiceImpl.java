package ru.skypro.lessons.springboot.weblibrary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skypro.lessons.springboot.weblibrary.entity.Employee;
import ru.skypro.lessons.springboot.weblibrary.entity.Position;
import ru.skypro.lessons.springboot.weblibrary.entity.Report;
import ru.skypro.lessons.springboot.weblibrary.exceptions.ReportNotFoundException;
import ru.skypro.lessons.springboot.weblibrary.pojo.PositionReportPojo;
import ru.skypro.lessons.springboot.weblibrary.repository.EmployeeRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.PositionRepository;
import ru.skypro.lessons.springboot.weblibrary.repository.ReportRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final ReportRepository reportRepository;


    public ReportServiceImpl(EmployeeRepository employeeRepository,
                             PositionRepository positionRepository,
                             ReportRepository reportRepository) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public int createReport() throws JsonProcessingException {
        log.info("Create report method is invoked");
        List<Position> positions = (List<Position>) positionRepository.findAll();
        log.debug("Successfully found all positions from DB");
        List<PositionReportPojo> pojoList = new ArrayList<>();
        for (Position position : positions) {
            List<Employee> employees = employeeRepository.findAllByPosition(position);
            log.debug("Successfully found all employees by position {}", position.getName());

            //calculate employees
            int numberOfEmployees = employees.size();
            //find max salary
            int maxSalary = 0;
            for (Employee employee : employees) {
                if (employee.getSalary() > maxSalary) {
                    maxSalary = employee.getSalary();
                }
            }
            //find min salary
            int minSalary = Integer.MAX_VALUE;
            for (Employee employee : employees) {
                if (employee.getSalary() < minSalary) {
                    minSalary = employee.getSalary();
                }
            }
            //find avg salary
            int salary = 0;
            for (Employee employee : employees) {
                salary += employee.getSalary();
            }
            int avgSalary = salary / employees.size();

            PositionReportPojo positionReportPojo = new PositionReportPojo(position.getName(),
                    numberOfEmployees, maxSalary, minSalary, avgSalary);
            pojoList.add(positionReportPojo);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        try {
            json = objectMapper.writeValueAsString(pojoList);
        } catch (JsonProcessingException e) {
            log.error("Could not process report to JSON", e);
            throw e;
        }

        Report report = new Report();
        report.setReportData(json);
        report.setReportDate(new Date());

        Report savedReport = reportRepository.save(report);
        log.debug("Successfully saved report to DB");
        return savedReport.getId();
    }

    @Override
    public String getReportJsonString(int id) throws ReportNotFoundException {
        log.info("Get report JSON string method is invoked");
        try {
            Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found!"));
            log.debug("Successfully found report by id = {}", id);
            return report.getReportData();
        } catch (ReportNotFoundException e) {
            log.error("Could not find report by id = {}", id, e);
            throw e;
        }
    }
}
