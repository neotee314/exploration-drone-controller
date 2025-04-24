package com.neotee.exploration_drone_controller.planet.application;

import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.planet.domain.Drone;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface DroneServiceInterface {
    Drone findDroneById(UUID droneId);

    UUID getDronePlanet(UUID droneId);


    void save(Drone drone);
}
