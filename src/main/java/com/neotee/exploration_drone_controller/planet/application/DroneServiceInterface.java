package com.neotee.exploration_drone_controller.planet.application;

import com.neotee.exploration_drone_controller.explorationdrone.application.ExplorationDroneDTO;
import com.neotee.exploration_drone_controller.planet.domain.Drone;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface DroneServiceInterface {
    Drone findDroneById(UUID droneId);

    void save(Drone drone);

    void validateSpawn(UUID droneId);

    Drone validateDroneExists(UUID droneId);

    List<ExplorationDroneDTO> getAllDrones();
}
