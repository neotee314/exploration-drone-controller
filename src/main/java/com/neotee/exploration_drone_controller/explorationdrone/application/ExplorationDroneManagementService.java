package com.neotee.exploration_drone_controller.explorationdrone.application;

import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.PlanetService;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExplorationDroneManagementService {

    private final ExplorationDroneRepository explorationDroneRepository;
    private final DroneValidator droneValidator;
    private final PlanetService planetService;


    @Transactional
    public void spawn(UUID droneId) {
        droneValidator.validateSpawn(droneId);
        ExplorationDrone explorationDrone = new ExplorationDrone(droneId);
        Planet spaceStation = planetService.getSpaceStation();
        spaceStation.addDrone(explorationDrone);
        spaceStation.markPlanetVisited();
        explorationDroneRepository.save(explorationDrone);
        planetService.save(spaceStation);
    }


    public Load getExplorationDroneLoad(UUID droneId) {
        ExplorationDrone explorationDrone = droneValidator.validateDroneExists(droneId);
        return explorationDrone.getLoad();
    }

    public UUID getDronePlanet(UUID droneId) {
        ExplorationDrone explorationDrone = droneValidator.validateDroneExists(droneId);
        return explorationDrone.getPlanet().getPlanetId();
    }


    public void deleteAll() {
        explorationDroneRepository.deleteAll();
    }


}
