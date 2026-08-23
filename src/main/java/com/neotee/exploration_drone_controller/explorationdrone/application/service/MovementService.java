package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.repository.ExplorationDroneRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MovementService {

    private final ExplorationDroneRepository explorationDroneRepository;
    private final HyperspaceTunnelExitFinder hyperspaceTunnelExitFinder;

   
    public void move(ExplorationDrone drone, CompassPoint moveDirection) {
        drone.move(moveDirection);
        explorationDroneRepository.save(drone);
    }

   
    public void explore(ExplorationDrone drone) {
        drone.explore();
        explorationDroneRepository.save(drone);
    }

   
    public void gohome(ExplorationDrone drone) {
        drone.gohome();
        explorationDroneRepository.save(drone);
    }

   
    public void transport(ExplorationDrone drone) {
        var exitPlanet =
                hyperspaceTunnelExitFinder.findByEntryPlanet(drone.getPlanet());

        drone.transport(exitPlanet);
        explorationDroneRepository.save(drone);
    }
}