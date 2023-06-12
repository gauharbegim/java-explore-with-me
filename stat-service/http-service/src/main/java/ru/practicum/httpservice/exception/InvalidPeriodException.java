package ru.practicum.httpservice.exception;

public class InvalidPeriodException extends RuntimeException {
    public InvalidPeriodException(String message) {
        super(message);
    }
}