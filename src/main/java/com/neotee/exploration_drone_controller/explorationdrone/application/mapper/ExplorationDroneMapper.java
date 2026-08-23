package com.neotee.exploration_drone_controller.explorationdrone.application.mapper;

import com.neotee.exploration_drone_controller.explorationdrone.application.dto.ExplorationDroneResponseDTO;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import org.springframework.stereotype.Component;

@Component
public class ExplorationDroneMapper {
    private CommandMapper commandMapper;

    public ExplorationDroneResponseDTO toDTO(ExplorationDrone drone) {
        var dto = new ExplorationDroneResponseDTO();
        dto.setId(drone.getId());
        dto.setName(drone.getName());
        dto.setPlanetId(drone.getPlanet().getId());
        dto.setCommandHistory(drone.getCommandHistory().stream().map(commandMapper::toDTO).toList());
        return dto;
    }

    public ExplorationDrone toEntity(ExplorationDroneResponseDTO dto) {
        var drone = new ExplorationDrone();

        drone.setId(dto.getId());
        drone.setName(dto.getName());
        drone.setCommandHistory(dto.getCommandHistory().stream().map(commandMapper::toCommand).toList());

        if (dto.getPlanetId() != null) {
            var planet = new Planet();
            planet.setId(dto.getPlanetId());
            drone.setPlanet(planet);
        }

        return drone;
    }
}