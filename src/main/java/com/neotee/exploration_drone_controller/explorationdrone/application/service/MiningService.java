package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.UraniumComparator;
import com.neotee.exploration_drone_controller.exceptions.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.repository.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.explorationdrone.domain.service.MiningPolicyServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class MiningService implements MiningPolicyServiceInterface {
    private final ExplorationDroneRepository explorationDroneRepository;

    public void mine(ExplorationDrone drone) {
        if (!canMine(drone)) throw new ExplorationDroneControlException("cannot mine");
        drone.mine();
        explorationDroneRepository.save(drone);
    }

    @Override
    public Boolean canMine(ExplorationDrone explorationDrone) {
        var drones = explorationDroneRepository.findAll();
        var dronesOnPlanet = new ArrayList<>(drones.stream().filter(drone -> drone.getPlanet().equals(explorationDrone.getPlanet())).toList());
        dronesOnPlanet.sort(Comparator.comparing(ExplorationDrone::getUranium, new UraniumComparator()));
        return dronesOnPlanet.getFirst().equals(explorationDrone);
    }
}
