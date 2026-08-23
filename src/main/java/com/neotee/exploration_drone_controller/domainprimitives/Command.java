package com.neotee.exploration_drone_controller.domainprimitives;

import com.neotee.exploration_drone_controller.exceptions.ExplorationDroneControlException;
import jakarta.persistence.Column;
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

    private UUID droneId;

    private Command(String command, UUID droneId) {
        this.command = command;
        this.droneId = droneId;
    }

    public static Command fromCommandString(String input) {
        if (input == null || !input.startsWith("[") || !input.endsWith("]")) {
            throw new ExplorationDroneControlException("Invalid command format");
        }

        var parts = input.substring(1, input.length() - 1).split(",", 2);

        if (parts.length != 2) {
            throw new ExplorationDroneControlException("Invalid command format");
        }

        var command = parts[0];
        var uuidString = parts[1];

        if (!isValidCommand(command)) {
            throw new ExplorationDroneControlException("Unknown command: " + command);
        }

        try {
            var droneId = UUID.fromString(uuidString);
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
