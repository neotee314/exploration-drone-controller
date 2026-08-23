package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import com.neotee.exploration_drone_controller.explorationdrone.domain.repository.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.service.DroneDeletionService;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExplorationDroneDeletionService implements DroneDeletionService {

    private final ExplorationDroneRepository explorationDroneRepository;

    @Override
    public void deleteByPlanet(Planet planet) {
        explorationDroneRepository.deleteByPlanet(planet);
    }
}