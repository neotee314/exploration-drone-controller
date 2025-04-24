package com.neotee.exploration_drone_controller.planet.application;


import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.explorationdrone.application.DroneValidator;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.planet.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlanetService {

    private final PlanetRepository planetRepository;
    private final PlanetValidator planetValidator;
    private final DroneValidator droneValidator;
    private final DroneServiceInterface droneServiceInterface;

    public void save(Planet planet) {
        planetRepository.save(planet);
    }



    @Transactional
    public UUID findExplorationDronePlanet(UUID droneId) {
        Drone drone = droneValidator.validateDroneExists(droneId);
        List<Planet> planets = getAllPlanet();
        for (Planet planet : planets) {
            if (planet.contains(drone)) {
                return planet.getId();
            }
        }
        return null;
    }


    private List<Planet> getAllPlanet() {
        List<Planet> planets = new ArrayList<>();
        for (Planet planet : planetRepository.findAll()) {
            planets.add(planet);
        }
        return planets;
    }



    public Uranium getUranium(UUID planetId) {
        Planet planet = planetValidator.validatePlanetExists(planetId);
        return planet.getUranium();
    }

    public PlanetType getPlanetType(UUID planetId) {
        Planet planet = planetValidator.validatePlanetExists(planetId);
        return planet.getPlanetType();
    }


    @Transactional
    public List<UUID> getDronesOf(UUID planetId) {
        Planet planet = planetValidator.validatePlanetExists(planetId);
        List<Drone> drones = planet.getDrones();
        List<UUID> droneIds = new ArrayList<>();
        drones.forEach(drone -> droneIds.add(drone.getId()));
        return droneIds;
    }


    public void addToUranium(UUID planetId, Uranium uranium) {
        Planet planet = planetValidator.validatePlanetExists(planetId);
        if(uranium==null) throw new ExplorationDroneControlException("Uranium cannot be null");
        if (!uranium.isGreaterEqualThan(Uranium.fromAmount(0)))
            throw new ExplorationDroneControlException("Uranium quantity cannot be negative");
        planet.addToUranium(uranium);
        planetRepository.save(planet);
    }


    public Planet findPlanetById(UUID planetId) {
        return planetRepository.findById(planetId).orElse(null);
    }

    public void deleteAll() {
        planetRepository.deleteAll();
    }



    public List<UUID> getPlanets() {
        List<Planet> planets = getAllPlanet();
        List<UUID> planetIds = new ArrayList<>();
        planets.forEach(planet -> planetIds.add(planet.getId()));
        return planetIds;
    }

    public UUID resetAll() {
        planetRepository.deleteAll();
        SpaceStation spaceStation = new SpaceStation();
        planetRepository.save(spaceStation);
        return spaceStation.getId();
    }
    public Planet getSpaceStation() {
        List<Planet> planets = getAllPlanet();
        for (Planet planet : planets) {
            if (planet.isSpaceStation()) {
                return  planet;
            }
        }
        throw new ExplorationDroneControlException("No SpaceStation found");
    }

    @Transactional
    public void spawn(UUID droneId) {
        droneValidator.validateSpawn(droneId);
        ExplorationDrone explorationDrone = new ExplorationDrone(droneId);

        Planet spaceStation = getSpaceStation();
        spaceStation.addDrone(explorationDrone);
        spaceStation.markPlanetVisited();
        planetRepository.save(spaceStation);
    }
}
