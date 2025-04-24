package com.neotee.exploration_drone_controller.explorationdrone.application;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDroneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DroneValidator {
    private final ExplorationDroneRepository explorationDroneRepository;

    public void validateSpawn(UUID droneId) {
        if (droneId == null) throw new ExplorationDroneControlException("droneId is null");
        ExplorationDrone explorationDrone = explorationDroneRepository.findById(droneId).orElse(null);
        if(explorationDrone!=null) throw new ExplorationDroneControlException("droneId "+droneId+" already exists");
    }


    public ExplorationDrone validateDroneExists(UUID droneId) {
        if (droneId == null) throw new ExplorationDroneControlException("droneId is null");
        ExplorationDrone explorationDrone = explorationDroneRepository.findById(droneId).orElseThrow(() ->
                new ExplorationDroneControlException("drone do not exists"));
        return explorationDrone;
    }

    public void validateDroneId(UUID droneId) {
        if (droneId == null) throw new ExplorationDroneControlException("droneId is null");
    }
}
