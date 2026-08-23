package com.neotee.exploration_drone_controller.exceptions;

//422
public class DomainValidationException extends ExplorationDroneControlException {
    private final String field;

    public DomainValidationException(String field, String message) {
        super(field + ": " + message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}