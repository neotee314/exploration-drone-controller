package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.ExplorationDroneId;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.repository.ExplorationDroneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpawnService {

    private final SpaceStationFinder planetServieInterface;
    private final ExplorationDroneRepository explorationDroneRepository;

    public ExplorationDrone spawn() {
        var droneId = ExplorationDroneId.newId();
        var spaceSation = planetServieInterface.getSpaceStation();
        var drone = ExplorationDrone.create(spaceSation, droneId);
        return explorationDroneRepository.save(drone);
    }


    public ExplorationDrone spawnById(ExplorationDroneId droneId) {
        var spaceSation = planetServieInterface.getSpaceStation();
        var drone = ExplorationDrone.create(spaceSation, droneId);
        return explorationDroneRepository.save(drone);
    }


}
