package com.neotee.exploration_drone_controller.explorationdrone.application.dto;


import java.util.List;
import java.util.UUID;

public record ExplorationDroneResponseDTO(
        String name,
        UUID id,
        UUID planetId,
        List<CommandResponseDto> commandHistory) {
}
