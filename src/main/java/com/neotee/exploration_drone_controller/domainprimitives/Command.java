package com.neotee.exploration_drone_controller.domainprimitives;

import certification.ExplorationDroneControlException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class Command {

    private static final Set<String> COMPASS_DIRECTIONS = Set.of("north", "south", "east", "west");
    private static final Set<String> SIMPLE_COMMANDS = Set.of("spawn", "transport", "explore", "gohome", "mine");

    private String command;

    private UUID explorationDroneId;

    private Command(String command, UUID droneId) {
        this.command = command;
        this.explorationDroneId = droneId;
    }

    public static Command fromCommandString(String input) {
        if (input == null || !input.matches("\\[\\w+,[a-fA-F0-9\\-]+\\]")) {
            throw new ExplorationDroneControlException("Invalid command format");
        }

        String[] parts = input.substring(1, input.length() - 1).split(",", 2);
        String command = parts[0];
        String uuidString = parts[1];

        if (!isValidCommand(command)) {
            throw new ExplorationDroneControlException("Unknown command: " + command);
        }

        try {
            UUID droneId = UUID.fromString(uuidString);
            return new Command(command, droneId);
        } catch (IllegalArgumentException e) {
            throw new ExplorationDroneControlException("Invalid UUID format");
        }
    }

    private static boolean isValidCommand(String command) {
        return SIMPLE_COMMANDS.contains(command) || COMPASS_DIRECTIONS.contains(command);
    }

    public String getCommandString() {
        return command;
    }

    public boolean isMove() {
        return COMPASS_DIRECTIONS.contains(command);
    }

    public CompassPoint getMoveDirection() {
        return isMove() ? CompassPoint.fromString(command) : null;
    }

    public boolean isSpawn() {
        return "spawn".equals(command);
    }

    public boolean isExplore() {
        return "explore".equals(command);
    }

    public boolean isGohome() {
        return "gohome".equals(command);
    }

    public boolean isTransport() {
        return "transport".equals(command);
    }

    public boolean isMine() {
        return "mine".equals(command);
    }
}
