package com.neotee.exploration_drone_controller.planet.application.service;

import com.neotee.exploration_drone_controller.planet.domain.service.MiningPolicyInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MiningPolicyService implements MiningPolicyInterface {
/***
    @Override
    public Boolean canMine(Planet planet, Drone drone) {
        if (planet == null || drone == null) throw new ExplorationDroneControlException("Planet or Drone is null");
        var drones = planet.getDrones();
        if (drones.isEmpty() || !drones.contains(drone))
            throw new ExplorationDroneControlException("Drone is not on this planet");
        drones.sort(Comparator.comparing(Drone::getUranium, new UraniumComparator()));
        return drones.getFirst().equals(drone);
    }***/
}
