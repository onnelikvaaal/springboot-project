package ru.skypro.lessons.springboot.weblibrary.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeePojo {

    private int id;
    private String name;
    private int salary;
}