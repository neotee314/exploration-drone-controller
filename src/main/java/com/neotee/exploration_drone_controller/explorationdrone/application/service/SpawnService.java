package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.ExplorationDroneId;
import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.repository.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.service.PlanetService;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpawnService {

    private final ExplorationDroneRepository explorationDroneRepository;
    private final PlanetService planetService;


    @Transactional
    public void spawn(ExplorationDroneId droneId) {
        var spaceSation = planetService.getSpaceStation();
        var drone = ExplorationDrone.create(spaceSation, droneId);
        explorationDroneRepository.save(drone);
    }


    public Load getExplorationDroneLoad(ExplorationDroneId droneId) {
        ExplorationDrone explorationDrone = droneValidator.validateDroneExists(droneId);
        return explorationDrone.getLoad();
    }

    public Planet getDronePlanet(ExplorationDroneId droneId) {
        ExplorationDrone explorationDrone = droneValidator.validateDroneExists(droneId);
        return explorationDrone.getPlanet().getId();
    }


    public void deleteAll() {
        explorationDroneRepository.deleteAll();
    }


}
