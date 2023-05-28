package ru.skypro.lessons.springboot.weblibrary.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.lessons.springboot.weblibrary.exceptions.ReportNotFoundException;
import ru.skypro.lessons.springboot.weblibrary.service.ReportService;

@RestController
@RequestMapping("/report")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/")
    public int createReport() throws JsonProcessingException {
        return reportService.createReport();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getReportById(@PathVariable int id) throws ReportNotFoundException {
        String fileName = "report" + id + ".json";
        String json = reportService.getReportJsonString(id);
        Resource resource = new ByteArrayResource(json.getBytes());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(resource);
    }
}
