package ru.skypro.lessons.springboot.weblibrary.exceptions;

public class EmployeeNotFoundException extends Exception {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
