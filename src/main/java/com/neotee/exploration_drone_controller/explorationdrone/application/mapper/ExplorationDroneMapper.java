package com.neotee.exploration_drone_controller.explorationdrone.application.mapper;

import com.neotee.exploration_drone_controller.explorationdrone.application.dto.ExplorationDroneResponseDTO;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExplorationDroneMapper {

    private final CommandMapper commandMapper;

    public ExplorationDroneResponseDTO toDto(ExplorationDrone drone) {
        return new ExplorationDroneResponseDTO(
                drone.getName(),
                drone.getId().getId(),
                drone.getPlanet().getId().getId(),
                drone.getCommandHistory().stream().map(commandMapper::toDTO).toList());
    }
}