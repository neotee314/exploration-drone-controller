package com.neotee.exploration_drone_controller.explorationdrone.application.mapper;

import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommandMapper {

    default Command toCommand(CommandRequestDto commandDTO) {
        if (commandDTO == null) {
            return null;
        }

        String commandString = "[" + commandDTO.getCommandString() + "," + commandDTO.getExplorationDroneId() + "]";
        return Command.fromCommandString(commandString);
    }

    default CommandRequestDto toDTO(Command command) {
        if (command == null) {
            return null;
        }
        return new CommandRequestDto(
                command.getCommandString(),
                command.getExplorationDroneId()
        );
    }
}
