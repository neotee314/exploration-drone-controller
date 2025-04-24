package com.neotee.exploration_drone_controller.explorationdrone.application;


import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.PlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MiningService {

    private final PlanetService planetService;
    private final ExplorationDroneRepository explorationDroneRepository;
    private final DroneValidator droneValidator;

    @Transactional
    public void mine(UUID droneId) {
        ExplorationDrone drone = droneValidator.validateDroneExists(droneId);
        drone.mine();
        explorationDroneRepository.save(drone);
    }


}
