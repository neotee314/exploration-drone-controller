package com.neotee.exploration_drone_controller.explorationdrone.application;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDroneRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MovementService {

    private final ExplorationDroneRepository explorationDroneRepository;
    private final DroneValidator droneValidator;

    @Transactional
    public void move(UUID droneId, CompassPoint direction) {
        ExplorationDrone explorationDrone = droneValidator.validateDroneExists(droneId);

        explorationDrone.move(direction);

        explorationDroneRepository.save(explorationDrone);

    }

    @Transactional
    public void explore(UUID droneId) {
        ExplorationDrone drone = droneValidator.validateDroneExists(droneId);
        drone.explore();
        explorationDroneRepository.save(drone);
    }

    @Transactional
    public void goHome(UUID droneId) {
        ExplorationDrone drone = droneValidator.validateDroneExists(droneId);
        drone.gohome();
        explorationDroneRepository.save(drone);
    }

    @Transactional
    public void transport(UUID droneId) {

        ExplorationDrone drone = droneValidator.validateDroneExists(droneId);

        drone.transport();

        explorationDroneRepository.save(drone);


    }
}
