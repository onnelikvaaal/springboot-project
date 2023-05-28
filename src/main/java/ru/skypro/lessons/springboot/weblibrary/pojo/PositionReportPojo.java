package ru.skypro.lessons.springboot.weblibrary.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PositionReportPojo {
    private String positionName;
    private int numberOfEmployees;
    private int maximumSalary;
    private int minimumSalary;
    private int averageSalary;
}
