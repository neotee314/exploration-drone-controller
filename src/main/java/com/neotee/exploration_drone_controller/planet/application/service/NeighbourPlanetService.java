package com.neotee.exploration_drone_controller.planet.application.service;


import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.exceptions.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import com.neotee.exploration_drone_controller.planet.domain.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NeighbourPlanetService {

    private final PlanetRepository planetRepository;
    private final NeighbourSearchService neighbourSearchService;

    @Transactional
    public void createNeighborOf(UUID planetId, UUID neighbourId, CompassPoint direction) {
        if (planetId == null) throw new ExplorationDroneControlException("PlanetId is null");
        var planet = planetRepository.findById(planetId).orElseThrow(() -> new ExplorationDroneControlException("Planet not found"));
        if (neighbourId == null) return;
        var neighbour =  planetRepository.findById(planetId)
                .orElseGet(() -> {
                    var found = new Planet();
                    found.setId(planetId);
                    return found;
                });

        neighbour = planetRepository.save(neighbour);

        planet.addNeighbour(neighbour, direction);
        neighbour.addNeighbour(planet, direction.oppositeDirection());
        neighbourSearchService.findAllNeighbors(neighbour);
        planetRepository.save(planet);
        planetRepository.save(neighbour);

    }




}
