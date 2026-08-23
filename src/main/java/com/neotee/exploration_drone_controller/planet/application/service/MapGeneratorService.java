package com.neotee.exploration_drone_controller.planet.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import com.neotee.exploration_drone_controller.planet.domain.model.SpaceStation;
import com.neotee.exploration_drone_controller.planet.domain.repository.PlanetRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapGeneratorService {

    private final PlanetRepository planetRepository;
    private final EntityManager entityManager;

    private final Random random = new Random();

    @Transactional
    public Planet generateMap(int planetCount) {
        planetRepository.deleteAll();
        if (planetCount < 1)
            throw new DomainValidationException("MapGeneratorService", "Planet count must be greater than or equal to 1");


        planetRepository.deleteAll();

        var positions = new HashMap<Position, Planet>();
        var planets = new ArrayList<Planet>();

        // Space Station at (0,0)
        var spaceStation = SpaceStation.create();
        var stationPosition = new Position(0, 0);

        positions.put(stationPosition, spaceStation);
        planets.add(spaceStation);

        // Generate connected map
        while (planets.size() < planetCount) {

            var parentPosition =
                    findRandomPositionWithFreeNeighbour(positions);

            var parent = positions.get(parentPosition);

            var direction =
                    findRandomFreeDirection(parentPosition, positions);

            var newPosition =
                    move(parentPosition, direction);

            var planet = Planet.create();

            planet.setName(generatePlanetName());

            planet.addToUranium(
                    Uranium.fromAmount(random.nextInt(101))
            );

            positions.put(newPosition, planet);
            planets.add(planet);

            // This creates the initial connection in both directions
            parent.addNeighbour(planet, direction);
        }


        completeNeighbourConnections(positions);


        // Persist the complete map
        planets.forEach(entityManager::persist);
        entityManager.flush();

        log.info("Generated map containing {} planets. Complete paths", planetCount);

        return spaceStation;
    }

    private void completeNeighbourConnections(
            Map<Position, Planet> positions
    ) {

        int completedConnections = 0;

        for (var entry : positions.entrySet()) {

            Position position = entry.getKey();
            Planet planet = entry.getValue();

            for (var direction : CompassPoint.values()) {

                Position neighbourPosition =
                        move(position, direction);

                Planet neighbour =
                        positions.get(neighbourPosition);

                if (neighbour == null)
                    continue;

                if (planet.getNeighbourOf(direction) != null)
                    continue;


                planet.addNeighbour(neighbour, direction);

                completedConnections++;
            }
        }

        log.info("Completed {} missing neighbour connections", completedConnections);
    }

    private Position findRandomPositionWithFreeNeighbour(Map<Position, Planet> positions) {

        var candidates = positions.keySet()
                .stream()
                .filter(position -> hasFreeNeighbour(position, positions))
                .toList();

        if (candidates.isEmpty()) {
            throw new DomainValidationException("MapGeneratorService", "Could not generate a connected planet map");
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private boolean hasFreeNeighbour(Position position, Map<Position, Planet> positions) {

        for (var direction : CompassPoint.values()) {
            Position neighbourPosition = move(position, direction);
            if (!positions.containsKey(neighbourPosition))
                return true;

        }

        return false;
    }

    private CompassPoint findRandomFreeDirection(Position position, Map<Position, Planet> positions) {

        var directions = new ArrayList<>(List.of(CompassPoint.values()));

        Collections.shuffle(directions, random);

        for (var direction : directions) {

            Position neighbourPosition =
                    move(position, direction);

            if (!positions.containsKey(neighbourPosition)) {
                return direction;
            }
        }

        throw new DomainValidationException("MapGeneratorService", "No free direction available");
    }

    private Position move(
            Position position,
            CompassPoint direction
    ) {

        return switch (direction) {

            case NORTH -> new Position(
                    position.x(),
                    position.y() + 1
            );

            case SOUTH -> new Position(
                    position.x(),
                    position.y() - 1
            );

            case EAST -> new Position(
                    position.x() + 1,
                    position.y()
            );

            case WEST -> new Position(
                    position.x() - 1,
                    position.y()
            );
        };
    }

    private String generatePlanetName() {
        return "Planet-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private record Position(int x, int y) {
    }
}