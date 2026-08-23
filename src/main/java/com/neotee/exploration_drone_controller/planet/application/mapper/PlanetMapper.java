package com.neotee.exploration_drone_controller.planet.application.mapper;

import com.neotee.exploration_drone_controller.planet.application.dto.PlanetResponseDto;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import org.springframework.stereotype.Component;

@Component
public class PlanetMapper {

    public PlanetResponseDto toDto(Planet planet) {
        return new PlanetResponseDto(
                planet.getId().getId(),

                planet.getNorth() != null
                        ? planet.getNorth().getId().getId()
                        : null,

                planet.getEast() != null
                        ? planet.getEast().getId().getId()
                        : null,

                planet.getSouth() != null
                        ? planet.getSouth().getId().getId()
                        : null,

                planet.getWest() != null
                        ? planet.getWest().getId().getId()
                        : null,

                planet.getPlanetType().getValue(),

                planet.getUranium() != null
                        ? planet.getUranium().getAmount()
                        : 0
        );
    }
}