package ru.skypro.lessons.springboot.weblibrary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.skypro.lessons.springboot.weblibrary.exceptions.ReportNotFoundException;

public interface ReportService {

    int createReport() throws JsonProcessingException;

    String getReportJsonString(int id) throws ReportNotFoundException;
}
