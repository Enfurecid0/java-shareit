package ru.practicum.shareit.exceptions;

public class ConditionsNotMatchException extends RuntimeException {
    public ConditionsNotMatchException(String message) {
        super(message);
    }
}