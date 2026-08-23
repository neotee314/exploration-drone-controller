package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.repository.ExplorationDroneRepository;

import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class MovementService {

    private final ExplorationDroneRepository explorationDroneRepository;
    private final DroneValidator droneValidator;

    public void move(ExplorationDrone drone, CompassPoint direction) {
        drone.move(direction);
        explorationDroneRepository.save(drone);

    }

    public void explore(ExplorationDrone drone) {
        drone.explore();
        explorationDroneRepository.save(drone);
    }

    public void goHome(ExplorationDrone drone) {
        drone.gohome();
        explorationDroneRepository.save(drone);
    }

    public void transport(ExplorationDrone drone, Planet planet) {
        drone.transport(planet);
        explorationDroneRepository.save(drone);


    }
}
