package com.neotee.exploration_drone_controller.domainprimitives;

import com.neotee.exploration_drone_controller.exceptions.ExplorationDroneControlException;


public enum CompassPoint {
    NORTH, EAST, SOUTH, WEST;


    public static CompassPoint fromString(String directionString) {
        if (directionString == null || directionString.isBlank()) throw new ExplorationDroneControlException("Direction cannot be null");

        return switch (directionString.toLowerCase()) {
            case "north" -> NORTH;
            case "east" -> EAST;
            case "south" -> SOUTH;
            case "west" -> WEST;
            default -> throw new ExplorationDroneControlException(
                    String.format("'%s' is not a valid compass direction", directionString)
            );
        };
    }


    public CompassPoint oppositeDirection() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }


    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
