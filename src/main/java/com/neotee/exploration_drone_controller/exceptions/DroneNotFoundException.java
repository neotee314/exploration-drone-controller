package com.neotee.exploration_drone_controller.exceptions;

public class DroneNotFoundException extends ExplorationDroneControlException {

    private final String field;

    public DroneNotFoundException(String field, String message) {
        super(field + ": " + message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}