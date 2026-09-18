package br.com.rennataarruda.todolist.dto.auth;

public record FirstAccessAvailabilityRequest(
        FirstAccessAvailabilityType type,
        String value
) {
}
