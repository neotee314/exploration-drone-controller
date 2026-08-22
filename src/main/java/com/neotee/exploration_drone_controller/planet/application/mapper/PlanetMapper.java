package com.neotee.exploration_drone_controller.planet.application.mapper;

import com.neotee.exploration_drone_controller.explorationdrone.application.mapper.UraniumMapper;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.planet.application.dto.PlanetResponseDto;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PlanetMapper {

    private final UraniumMapper uraniumMapper;

    public PlanetMapper(UraniumMapper uraniumMapper) {
        this.uraniumMapper = uraniumMapper;
    }

    public PlanetResponseDto toDTO(Planet planet) {
        var dto = new PlanetResponseDto();

        dto.setPlanetId(planet.getPlanetId());

        if (planet.getNorth() != null) {
            dto.setNorthId(planet.getNorth().getPlanetId());
        }

        if (planet.getEast() != null) {
            dto.setEastId(planet.getEast().getPlanetId());
        }

        if (planet.getSouth() != null) {
            dto.setSouthId(planet.getSouth().getPlanetId());
        }

        if (planet.getWest() != null) {
            dto.setWestId(planet.getWest().getPlanetId());
        }

        dto.setPlanetType(
                planet.getPlanetType() != null
                        ? planet.getPlanetType().getValue()
                        : null
        );

        dto.setUranium(
                planet.getUranium() != null
                        ? uraniumMapper.toDTO(planet.getUranium())
                        : null
        );


        return dto;
    }

    public Planet toEntity(PlanetResponseDto dto) {
        var planet = new Planet();

        planet.setPlanetId(dto.getPlanetId());

        planet.setPlanetType(
                dto.getPlanetType() != null
                        ? com.neotee.exploration_drone_controller.domainprimitives.PlanetType
                        .valueOf(dto.getPlanetType())
                        : null
        );

        if (dto.getNorthId() != null) {
            var north = new Planet();
            north.setPlanetId(dto.getNorthId());
            planet.setNorth(north);
        }

        if (dto.getEastId() != null) {
            var east = new Planet();
            east.setPlanetId(dto.getEastId());
            planet.setEast(east);
        }

        if (dto.getSouthId() != null) {
            var south = new Planet();
            south.setPlanetId(dto.getSouthId());
            planet.setSouth(south);
        }

        if (dto.getWestId() != null) {
            var west = new Planet();
            west.setPlanetId(dto.getWestId());
            planet.setWest(west);
        }


        return planet;
    }
}