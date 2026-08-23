package com.neotee.exploration_drone_controller.planet.application.mapper;

import com.neotee.exploration_drone_controller.planet.application.dto.PlanetResponseDto;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import org.springframework.stereotype.Component;

@Component
public class PlanetMapper {

    public PlanetResponseDto toDTO(Planet planet) {
        return PlanetResponseDto.builder()
                .planetId(planet.getId().getId())
                .northId(planet.getNorth() != null ? planet.getNorth().getId().getId() : null)
                .eastId(planet.getEast() != null ? planet.getEast().getId().getId() : null)
                .southId(planet.getSouth() != null ? planet.getSouth().getId().getId() : null)
                .westId(planet.getWest() != null ? planet.getWest().getId().getId() : null)
                .planetType(planet.getPlanetType().getValue())
                .uranium(planet.getUranium() != null ? planet.getUranium().getAmount() : null)
                .build();
    }
}