package ru.skypro.lessons.springboot.weblibrary.exceptions;

public class ReportNotFoundException extends Exception {
    public ReportNotFoundException(String message) {
        super(message);
    }
}
