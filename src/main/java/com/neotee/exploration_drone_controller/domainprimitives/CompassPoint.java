package com.neotee.exploration_drone_controller.domainprimitives;

import certification.ExplorationDroneControlException;

import java.util.Objects;

/**
 * Domain Primitive representing the four cardinal compass directions.
 */
public enum CompassPoint {
    NORTH, EAST, SOUTH, WEST;

    /**
     * Converts a direction string to a {@link CompassPoint} enum.
     *
     * @param directionString The direction in lowercase string format ("north", "east", etc.).
     * @return The matching {@link CompassPoint}.
     * @throws ExplorationDroneControlException if the input string is null or does not match any valid direction.
     */
    public static CompassPoint fromString(String directionString) {
        Objects.requireNonNull(directionString, "Direction string cannot be null");

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

    /**
     * Returns the opposite direction of the current compass point.
     *
     * @return The opposite {@link CompassPoint}.
     */
    public CompassPoint oppositeDirection() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    /**
     * Converts this enum constant to its lowercase string representation.
     *
     * @return A lowercase string representing the direction, e.g., "north".
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
