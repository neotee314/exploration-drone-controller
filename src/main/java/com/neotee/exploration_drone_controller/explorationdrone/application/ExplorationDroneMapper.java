package com.neotee.exploration_drone_controller.explorationdrone.application;

import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring", uses = {CommandMapper.class})
public interface ExplorationDroneMapper {

    // Mapping from ExplorationDrone to ExplorationDroneDTO
    @Mapping(target = "planetId", source = "planet.planetId")  // map planetId in DTO to planet.id in ExplorationDrone
    @Mapping(target = "id", source = "droneId")  // map droneId in ExplorationDrone to id in ExplorationDroneDTO
    @Mapping(target = "commandHistory", source = "commandHistory")  // ignore commandHistory field (if you don't need it)
    ExplorationDroneDTO toDTO(ExplorationDrone explorationDrone);

    // Mapping from ExplorationDroneDTO to ExplorationDrone
    @Mapping(target = "planet.planetId", source = "planetId")  // map planetId in DTO to planet.id in ExplorationDrone
    @Mapping(target = "droneId", source = "id")  // map id in DTO to droneId in ExplorationDrone
    @Mapping(target = "path", ignore = true)  // ignore path field
    @Mapping(target = "transportState", ignore = true)  // ignore transportState field
    @Mapping(target = "load", ignore = true)  // ignore load field
    @Mapping(target = "commandHistory", source = "commandHistory") // ignore commandHistory field (if you don't need it)
    ExplorationDrone toEntity(ExplorationDroneDTO explorationDroneDTO);
}