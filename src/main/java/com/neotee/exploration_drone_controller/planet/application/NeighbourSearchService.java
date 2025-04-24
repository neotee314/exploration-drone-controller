package com.neotee.exploration_drone_controller.planet.application;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import com.neotee.exploration_drone_controller.planet.domain.PlanetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.neotee.exploration_drone_controller.domainprimitives.CompassPoint.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class NeighbourSearchService {

    private final PlanetRepository planetRepository;

    @Transactional
    public void findAllNeighbors(Planet startPlanet) {
        if (startPlanet == null) return;
        Set<Planet> visited = new HashSet<>();
        Map<Planet, CompassPoint> neighbours = exploreNeighbors(startPlanet, "", visited);

        for (Map.Entry<Planet, CompassPoint> entry : neighbours.entrySet()) {
            startPlanet.addNeighbour(entry.getKey(), entry.getValue());
            entry.getKey().addNeighbour(startPlanet, entry.getValue().oppositeDirection());
            planetRepository.save( entry.getKey());
        }

        planetRepository.save(startPlanet);
    }



    private Map<Planet, CompassPoint> exploreNeighbors(Planet startPlanet, String path, Set<Planet> visited) {
        Map<Planet, CompassPoint> neighbours = new HashMap<>();

        if (startPlanet == null || visited.contains(startPlanet)) return neighbours;
        visited.add(startPlanet);

        for (CompassPoint dir : CompassPoint.values()) {
            if (!path.isEmpty()) {
                CompassPoint last = getDirectionFromChar(path.charAt(path.length() - 1));
                if (dir.equals(last.oppositeDirection())) continue;
            }

            //etNeighbour(dir);
            Planet neighbor = startPlanet.getNeighbourOf(dir);
            if (neighbor != null) {
                String newPath = path + dir.toString().charAt(0);
                CompassPoint neighborDirection = getMatchingDirection(newPath);

                if (neighborDirection != null && newPath.length() > 0) {
                    neighbours.put(neighbor, neighborDirection);
                }

                Map<Planet, CompassPoint> subNeighbours = exploreNeighbors(neighbor, newPath, visited);
                neighbours.putAll(subNeighbours);
            }
        }

        return neighbours;
    }


    private Planet getNeighbour(Planet startPlanet, String path) {
        Planet currentPlanet = startPlanet;
        for (char directionChar : path.toCharArray()) {
            CompassPoint direction = getDirectionFromChar(directionChar);
            if (direction != null) {
                currentPlanet = currentPlanet.getNeighbourOf(direction);
                if (currentPlanet == null) {
                    return null;
                }
            }
        }
        return currentPlanet;
    }

    private CompassPoint getDirectionFromChar(char directionChar) {
        switch (directionChar) {
            case 'n':
                return NORTH;
            case 's':
                return SOUTH;
            case 'e':
                return EAST;
            case 'w':
                return WEST;
            default:
                return null;
        }
    }

    private CompassPoint getMatchingDirection(String path) {
        int n = count(path, 'n');
        int s = count(path, 's');
        int e = count(path, 'e');
        int w = count(path, 'w');

        if (n == s + 1 && e == w && e > 0) return NORTH;
        if (s == n + 1 && e == w && e > 0) return SOUTH;
        if (e == w + 1 && n == s && s > 0) return EAST;
        if (w == e + 1 && n == s && s > 0) return WEST;

        return null;
    }

    private int count(String path, char c) {
        int count = 0;
        for (char ch : path.toCharArray()) {
            if (ch == c) count++;
        }
        return count;
    }
}
