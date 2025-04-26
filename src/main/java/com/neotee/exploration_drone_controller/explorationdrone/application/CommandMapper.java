package com.neotee.exploration_drone_controller.explorationdrone.application;

import com.neotee.exploration_drone_controller.domainprimitives.Command;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommandMapper {

    default Command toEntity(CommandDTO commandDTO) {
        if (commandDTO == null) {
            return null;
        }

        String commandString = "[" + commandDTO.getCommandString() + "," + commandDTO.getExplorationDroneId() + "]";
        return Command.fromCommandString(commandString);
    }

    default CommandDTO toDTO(Command command) {
        if (command == null) {
            return null;
        }
        return new CommandDTO(
                command.getCommandString(),
                command.getExplorationDroneId()
        );
    }
}
