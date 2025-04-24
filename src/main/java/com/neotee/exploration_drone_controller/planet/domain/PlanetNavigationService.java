package com.neotee.exploration_drone_controller.planet.domain;


import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.explorationdrone.domain.UraniumComparator;

import java.util.Comparator;
import java.util.List;

public class PlanetNavigationService {

    public boolean canDroneMine(Planet planet, Drone drone) {
        if (planet == null || drone == null)
            throw new ExplorationDroneControlException("Planet or Drone cannot be null");

        List<Drone> drones = planet.getDrones();
        if (drones.isEmpty() || !drones.contains(drone))
            throw new ExplorationDroneControlException("Drone is not on this planet");

        drones.sort(Comparator.comparing(Drone::getUranium, new UraniumComparator()));
        return drones.getFirst().equals(drone);
    }

    public CompassPoint getDirectionTo(Planet origin, Planet target) {
        if (origin == null || target == null)
            throw new ExplorationDroneControlException("Origin or Target planet cannot be null");

        if (origin.equals(target)) return null;

        if (target.equals(origin.getNorth())) return CompassPoint.NORTH;
        if (target.equals(origin.getSouth())) return CompassPoint.SOUTH;
        if (target.equals(origin.getEast())) return CompassPoint.EAST;
        if (target.equals(origin.getWest())) return CompassPoint.WEST;

        throw new ExplorationDroneControlException("Cannot determine direction to target planet");
    }

    public List<Planet> getVisitedNeighbours(Planet planet) {
        return planet.getVisitedNeighbours();
    }

    public List<Planet> getUnvisitedNeighbours(Planet planet) {
        return planet.getUnvisitedNeighbours();
    }

    public List<Planet> getAllNeighbours(Planet planet) {
        return planet.getNeighbours();
    }
}
