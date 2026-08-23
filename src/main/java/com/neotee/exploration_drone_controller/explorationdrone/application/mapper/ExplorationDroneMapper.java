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
        return ExplorationDroneResponseDTO.builder()
                .name(drone.getName())
                .id(drone.getId().getId())
                .planetId(drone.getPlanet().getId().getId())
                .commandHistory(drone.getCommandHistory().stream().map(commandMapper::toDTO).toList())
                .build();
    }
}