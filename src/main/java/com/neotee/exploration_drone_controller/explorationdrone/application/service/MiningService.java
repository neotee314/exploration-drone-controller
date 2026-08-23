package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.UraniumComparator;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;

import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.repository.ExplorationDroneRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class MiningService {
    private final ExplorationDroneRepository explorationDroneRepository;

    public void mine(ExplorationDrone drone) {
        if (!canMine(drone)) throw new DomainValidationException("MiningService", "mining not allowed");
        drone.mine();
        explorationDroneRepository.save(drone);
    }

    public Boolean canMine(ExplorationDrone explorationDrone) {
        var drones = explorationDroneRepository.findAll();
        var dronesOnPlanet = new ArrayList<>(drones.stream().filter(drone -> drone.getPlanet().equals(explorationDrone.getPlanet())).toList());
        dronesOnPlanet.sort(Comparator.comparing(ExplorationDrone::getUranium, new UraniumComparator()));
        return dronesOnPlanet.getFirst().equals(explorationDrone);
    }
}
