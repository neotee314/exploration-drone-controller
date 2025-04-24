package com.neotee.exploration_drone_controller.explorationdrone.application;


import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.MiningPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MiningService {
    private final MiningPolicyService miningPolicyService;
    private final ExplorationDroneRepository explorationDroneRepository;
    private final DroneValidator droneValidator;

    @Transactional
    public void mine(UUID droneId) {
        ExplorationDrone drone = droneValidator.validateDroneExists(droneId);
        if (!miningPolicyService.canMine(drone.getPlanet(), drone)) throw new ExplorationDroneControlException("cannot mine");
        drone.mine();
        explorationDroneRepository.save(drone);
    }


}
