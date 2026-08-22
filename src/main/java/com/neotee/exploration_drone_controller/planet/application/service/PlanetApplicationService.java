package com.neotee.exploration_drone_controller.planet.application.service;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import com.neotee.exploration_drone_controller.planet.domain.repository.PlanetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.neotee.exploration_drone_controller.domainprimitives.PlanetType.SPACE_STATION;
import static com.neotee.exploration_drone_controller.domainprimitives.PlanetType.UNKNOWN;

@Service
@RequiredArgsConstructor
public class PlanetApplicationService {

    private final PlanetRepository planetRepository;


    public Planet createPlanet() {
        var planet = new Planet();
        if (planetRepository.count() == 0) {
            planet.setPlanetType(SPACE_STATION);
            return planetRepository.save(planet);
        }
        planet.setPlanetType(UNKNOWN);
        return planetRepository.save(planet);
    }


    public void addNeighbour(Planet planet, Planet neighbour, CompassPoint direction) {
        planet.addNeighbour(neighbour, direction);
        neighbour.addNeighbour(planet, direction.oppositeDirection());
        planetRepository.save(planet);
        planetRepository.save(neighbour);
    }


    public List<Planet> getAllPlanets() {
        return planetRepository.findAll();
    }


    public Planet addUranium(Planet planet, Uranium uranium) {
        planet.addToUranium(uranium);
        return planetRepository.save(planet);
    }
}