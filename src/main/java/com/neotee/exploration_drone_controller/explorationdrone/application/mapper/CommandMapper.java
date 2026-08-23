package com.neotee.exploration_drone_controller.explorationdrone.application.mapper;

import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.domainprimitives.ExplorationDroneId;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandRequestDto;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CommandMapper {

    public Command toCommand(ExplorationDroneId droneId, CommandRequestDto dto) {
        return Command.fromCommandString("[" + dto.getCommandString() + "," + droneId.value() + "]");
    }

    public CommandResponseDto toDTO(Command command) {
        return CommandResponseDto.builder()
                .commandString(command.getCommandString())
                .build();
    }
}