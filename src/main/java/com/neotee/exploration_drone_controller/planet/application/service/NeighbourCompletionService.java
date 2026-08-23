package com.neotee.exploration_drone_controller.planet.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import com.neotee.exploration_drone_controller.planet.domain.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class NeighbourCompletionService {

    private final PlanetRepository planetRepository;

    @Transactional
    public void completeNeighbours(Planet startPlanet) {

        if (startPlanet == null) {
            return;
        }

        Map<Planet, Position> positions = calculatePositions(startPlanet);

        Map<Position, Planet> planetsByPosition = new HashMap<>();

        positions.forEach((planet, position) ->
                planetsByPosition.put(position, planet)
        );

        int completedConnections = 0;

        for (Map.Entry<Planet, Position> entry : positions.entrySet()) {

            var planet = entry.getKey();
            var position = entry.getValue();

            for (CompassPoint direction : CompassPoint.values()) {

                var neighbourPosition = move(position, direction);
                var neighbour = planetsByPosition.get(neighbourPosition);
                if (neighbour == null)
                    continue;
                if (planet.getNeighbourOf(direction) == null) {
                    planet.addNeighbour(neighbour, direction);
                    completedConnections++;
                }
            }
        }

        planetRepository.saveAll(positions.keySet());

        log.info("Completed {} missing neighbour connections for planet map", completedConnections);
    }

    private Map<Planet, Position> calculatePositions(Planet startPlanet) {

        Map<Planet, Position> positions = new HashMap<>();
        Queue<Planet> queue = new ArrayDeque<>();

        Position origin = new Position(0, 0);

        positions.put(startPlanet, origin);
        queue.add(startPlanet);

        while (!queue.isEmpty()) {

            Planet current = queue.poll();
            Position currentPosition = positions.get(current);

            for (CompassPoint direction : CompassPoint.values()) {

                Planet neighbour = current.getNeighbourOf(direction);

                if (neighbour == null) {
                    continue;
                }

                Position neighbourPosition =
                        move(currentPosition, direction);

                if (!positions.containsKey(neighbour)) {

                    positions.put(neighbour, neighbourPosition);
                    queue.add(neighbour);

                } else {

                    Position existingPosition = positions.get(neighbour);

                    if (!existingPosition.equals(neighbourPosition)) {
                        throw new DomainValidationException("NeighbourCompletionService", "Planet map contains inconsistent neighbour relationships"
                        );
                    }
                }
            }
        }

        return positions;
    }

    private Position move(Position position, CompassPoint direction) {

        return switch (direction) {
            case NORTH -> new Position(position.x(), position.y() + 1);

            case SOUTH -> new Position(position.x(), position.y() - 1);

            case EAST -> new Position(position.x() + 1, position.y());

            case WEST -> new Position(position.x() - 1, position.y());
        };
    }

    private record Position(int x, int y) {
    }
}