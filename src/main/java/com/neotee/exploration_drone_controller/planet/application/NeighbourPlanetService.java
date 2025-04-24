package com.neotee.exploration_drone_controller.planet.application;


import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import com.neotee.exploration_drone_controller.planet.domain.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NeighbourPlanetService {

    private final PlanetRepository planetRepository;
    private final NeighbourSearchService neighbourSearchService;
    private final PlanetValidator planetValidator;

    @Transactional
    public void createNeighborOf(UUID planetId, UUID neighbourId, CompassPoint direction) {
        Planet planet = planetValidator.validatePlanetExists(planetId);
        if (neighbourId == null) return;
        Planet neighbour = planetValidator.findOrCreatePlanet(neighbourId);

        neighbour = planetRepository.save(neighbour);

        planet.addNeighbour(neighbour, direction);
        neighbour.addNeighbour(planet, direction.oppositeDirection());


        neighbourSearchService.findAllNeighbors(neighbour);
        planetRepository.save(planet);
        planetRepository.save(neighbour);

    }




}
