package com.neotee.exploration_drone_controller.planet.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetType;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.explorationdrone.application.service.MinedPlanetSaver;
import com.neotee.exploration_drone_controller.explorationdrone.application.service.SpaceStationFinder;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service.HyperspaceEnergyTunnelDeletionService;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service.PlanetFinderInterface;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import com.neotee.exploration_drone_controller.planet.domain.model.SpaceStation;
import com.neotee.exploration_drone_controller.planet.domain.repository.PlanetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.neotee.exploration_drone_controller.domainprimitives.PlanetType.SPACE_STATION;
import static com.neotee.exploration_drone_controller.domainprimitives.PlanetType.UNKNOWN;

@Service
@RequiredArgsConstructor
public class PlanetApplicationService implements PlanetFinderInterface, SpaceStationFinder{

    private final PlanetRepository planetRepository;
    private final NeighbourSearchService neighbourSearchService;
    private final DroneDeletionService droneDeletionService;
    private final HyperspaceEnergyTunnelDeletionService hyperspaceEnergyTunnelDeletionService;

    public Planet createPlanet() {
        var planet = Planet.create();
        planet.setPlanetType(planetRepository.count() == 0 ? SPACE_STATION : UNKNOWN);
        return planetRepository.save(planet);
    }

    public Planet addNeighbour(PlanetId planetId, PlanetId neighbourId, CompassPoint direction) {
        var planet = findPlanetById(planetId);
        var neighbour = findPlanetById(neighbourId);
        planet.addNeighbour(neighbour, direction);
        neighbour.addNeighbour(planet, direction.oppositeDirection());
        planetRepository.save(neighbour);
        return planetRepository.save(planet);
    }

    public List<Planet> getAllPlanets() {
        return planetRepository.findAll();
    }

    public Planet addUranium(PlanetId planetId, Uranium uranium) {
        var planet = findPlanetById(planetId);
        planet.addToUranium(uranium);
        return planetRepository.save(planet);
    }

    @Override
    public Planet findPlanetById(PlanetId planetId) {
        return planetRepository.findById(planetId).orElseThrow(() -> new DomainValidationException("Planet", "Planet not found"));
    }

    public PlanetType getPlanetType(PlanetId planetId) {
        return findPlanetById(planetId).getPlanetType();
    }

    public Uranium getUranium(PlanetId planetId) {
        return findPlanetById(planetId).getUranium();
    }


    @Transactional
    public Planet resetAll() {

        var spaceStation = planetRepository
                .findFirstByPlanetType(SPACE_STATION)
                .orElse(null);

        var planets = planetRepository.findAll();

        planets.forEach(hyperspaceEnergyTunnelDeletionService::deleteByPlanet);
        planets.forEach(droneDeletionService::deleteByPlanet);

        if (spaceStation == null) {
            planetRepository.deleteAll();

            var newSpaceStation = SpaceStation.create();
            return planetRepository.save(newSpaceStation);
        }

        spaceStation.removeNeighbours();
        planetRepository.deleteByPlanetTypeNot(SPACE_STATION);

        return spaceStation;
    }

    public void addToUranium(PlanetId planetId, Uranium uranium) {
        var planet = findPlanetById(planetId);
        planet.addToUranium(uranium);
        planetRepository.save(planet);
    }

    public void createNeighborOf(PlanetId planetId, PlanetId neighbourId, CompassPoint direction) {
        if (neighbourId == null) return;
        var planet = findPlanetById(planetId);
        var neighbour = planetRepository.findById(neighbourId).orElseGet(() -> Planet.create(neighbourId));
        neighbour = planetRepository.save(neighbour);
        planet.addNeighbour(neighbour, direction);
        neighbour.addNeighbour(planet, direction.oppositeDirection());
        neighbourSearchService.findAllNeighbors(neighbour);
        planetRepository.save(planet);
        planetRepository.save(neighbour);
    }

    @Override
    public Planet getSpaceStation() {
        return planetRepository.findByPlanetType(SPACE_STATION).orElseThrow(() -> new DomainValidationException("SpaceStation", "Space station not found"));
    }

}