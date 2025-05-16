package com.neotee.exploration_drone_controller.explorationdrone.application;

import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CommandMapper.class})
public interface ExplorationDroneMapper {

    // Mapping from ExplorationDrone to ExplorationDroneDTO
    @Mapping(target = "planetId", source = "planet.planetId")
    @Mapping(target = "id", source = "droneId")
    @Mapping(target = "commandHistory", source = "commandHistory")
    @Mapping(target = "name", source = "name")
    ExplorationDroneDTO toDTO(ExplorationDrone explorationDrone);

    // Mapping from ExplorationDroneDTO to ExplorationDrone
    @Mapping(target = "planet.planetId", source = "planetId")
    @Mapping(target = "droneId", source = "id")
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "transportState", ignore = true)
    @Mapping(target = "load", ignore = true)
    @Mapping(target = "commandHistory", source = "commandHistory")
    @Mapping(target = "name", source = "name")
    ExplorationDrone toEntity(ExplorationDroneDTO explorationDroneDTO);

}