package com.neotee.exploration_drone_controller.planet.application;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.explorationdrone.application.UraniumMapper;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.planet.domain.Drone;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import com.neotee.exploration_drone_controller.planet.domain.PlanetRepository;
import com.neotee.exploration_drone_controller.planet.domain.PlanetType;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UraniumMapper.class})
public abstract class PlanetMapper {

    @Mapping(target = "northId", source = "north.planetId")
    @Mapping(target = "eastId", source = "east.planetId")
    @Mapping(target = "southId", source = "south.planetId")
    @Mapping(target = "westId", source = "west.planetId")
    @Mapping(target = "uranium", source = "uranium")
    @Mapping(target = "planetType", source = "planetType",qualifiedByName = "planetTypeToString")
    @Mapping(target = "drones", source = "drones", qualifiedByName = "mapDronesToUUIDs")
    public abstract PlanetDTO toDTO(Planet planet);

    @Mapping(target = "planetId", source = "planetId")
    @Mapping(target = "north", source = "northId", qualifiedByName = "uuidToPlanet")
    @Mapping(target = "east", source = "eastId", qualifiedByName = "uuidToPlanet")
    @Mapping(target = "south", source = "southId", qualifiedByName = "uuidToPlanet")
    @Mapping(target = "west", source = "westId", qualifiedByName = "uuidToPlanet")
    @Mapping(target = "uranium", source = "uranium")
    @Mapping(target = "planetType", source = "planetType", qualifiedByName = "stringToPlanetType")
    @Mapping(target = "drones", source = "drones", qualifiedByName = "uuidsToDrones")
    public abstract Planet toEntity(PlanetDTO dto);

    @Named("mapDronesToUUIDs")
    public List<UUID> mapDronesToUUIDs(List<Drone> drones) {
        if (drones == null) return new ArrayList<>();
        return drones.stream()
                .map(Drone::getDroneId)
                .collect(Collectors.toList());
    }

    @Named("uuidsToDrones")
    public List<Drone> uuidsToDrones(List<UUID> uuids) {
        if (uuids == null) return new ArrayList<>();
        return uuids.stream()
                .map(id -> {
                    Drone d = new ExplorationDrone();
                    d.setDroneId(id);
                    return d;
                })
                .collect(Collectors.toList());
    }

    @Autowired
    protected PlanetRepository planetRepository;

    @Named("uuidToPlanet")
    public Planet uuidToPlanet(UUID id) {
        if (id == null) return null;
        return planetRepository.findById(id)
                .orElseThrow(() -> new ExplorationDroneControlException("Planet with id " + id + " not found"));
    }
    @Named("planetTypeToString")
    public String planetTypeToString(PlanetType type) {
        return type != null ? type.getValue() : null;
    }

    @Named("stringToPlanetType")
    public PlanetType stringToPlanetType(String type) {
        if (type == null) return null;
        try {
            return PlanetType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new ExplorationDroneControlException("Invalid planet type: " + type);
        }
    }
}
