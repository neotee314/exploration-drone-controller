package com.neotee.exploration_drone_controller.explorationdrone.application;

import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.DroneServiceInterface;
import com.neotee.exploration_drone_controller.planet.domain.Drone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExplorationDroneService implements DroneServiceInterface {

    private final ExplorationDroneRepository explorationDroneRepository;
    private final DroneValidator droneValidator;



    public void deleteAll() {
        explorationDroneRepository.deleteAll();
    }

    public Load getExplorationDroneLoad(UUID droneId) {
        ExplorationDrone explorationDrone = droneValidator.validateDroneExists(droneId);
        return explorationDrone.getLoad();

    }




    @Override
    public Drone findDroneById(UUID droneId) {
        return droneValidator.validateDroneExists(droneId);
    }

    @Override
    public UUID getDronePlanet(UUID droneId) {
        ExplorationDrone explorationDrone = droneValidator.validateDroneExists(droneId);
        return explorationDrone.getPlanet().getId();
    }

    @Override
    public void save(Drone drone) {
        explorationDroneRepository.save((ExplorationDrone) drone);
    }
}
