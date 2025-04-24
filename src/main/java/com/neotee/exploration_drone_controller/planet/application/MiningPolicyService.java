package com.neotee.exploration_drone_controller.planet.application;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.explorationdrone.domain.UraniumComparator;
import com.neotee.exploration_drone_controller.planet.domain.Drone;
import com.neotee.exploration_drone_controller.planet.domain.MiningPolicyInterface;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class MiningPolicyService implements MiningPolicyInterface {

    @Override
    public Boolean canMine(Planet planet, Drone drone) {
        if (planet == null || drone == null) throw new ExplorationDroneControlException("Planet or Drone is null");
        var drones = planet.getDrones();
        if (drones.isEmpty() || !drones.contains(drone))
            throw new ExplorationDroneControlException("Drone is not on this planet");
        drones.sort(Comparator.comparing(Drone::getUranium, new UraniumComparator()));
        return drones.getFirst().equals(drone);
    }
}
