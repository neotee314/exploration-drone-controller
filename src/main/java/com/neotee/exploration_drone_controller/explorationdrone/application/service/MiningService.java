package com.neotee.exploration_drone_controller.explorationdrone.application.service;


import com.neotee.exploration_drone_controller.exceptions.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.repository.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.service.MiningPolicyService;
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
