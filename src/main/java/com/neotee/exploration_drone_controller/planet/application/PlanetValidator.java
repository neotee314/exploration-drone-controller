package com.neotee.exploration_drone_controller.planet.application;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import com.neotee.exploration_drone_controller.planet.domain.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanetValidator {
    private final PlanetRepository planetRepository;





    public Planet validatePlanetExists(UUID planetId) {
        if (planetId == null) throw new ExplorationDroneControlException("PlanetId is null");
        Planet planet = planetRepository.findById(planetId).orElseThrow(() -> new ExplorationDroneControlException("Planet not found"));
        return planet;
    }


    public CompassPoint validateDirectionExists(CompassPoint compassPoint) {
        if (compassPoint == null) throw new ExplorationDroneControlException("TunnelId is null");
        return compassPoint;
    }

    public Planet findOrCreatePlanet(UUID planetId) {
        return planetRepository.findById(planetId)
                .orElseGet(() -> {
                    Planet planet = new Planet();
                    planet.setId(planetId);
                    return planet;
                });
    }


}
