package com.neotee.exploration_drone_controller.planet.domain.model;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetType;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetVisitStatus;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.neotee.exploration_drone_controller.domainprimitives.CompassPoint.*;
import static com.neotee.exploration_drone_controller.domainprimitives.PlanetType.SPACE_STATION;
import static com.neotee.exploration_drone_controller.domainprimitives.PlanetType.UNKNOWN;
import static com.neotee.exploration_drone_controller.domainprimitives.PlanetVisitStatus.NOT_VISITED;
import static com.neotee.exploration_drone_controller.domainprimitives.PlanetVisitStatus.VISITED;

@Entity
@Getter
@Setter
public class Planet {

    @Id
    private UUID planetId;

    @OneToOne
    protected Planet north;
    @OneToOne
    protected Planet south;
    @OneToOne
    protected Planet west;
    @OneToOne
    protected Planet east;

    protected PlanetType planetType;

    @Embedded
    protected Uranium uranium;


    @Enumerated(EnumType.STRING)
    private PlanetVisitStatus visitStatus;


    private boolean isMined;


    public Planet() {
        this.planetId = UUID.randomUUID();
        this.uranium = Uranium.fromAmount(0);
        setPlanetType(UNKNOWN);
        this.isMined = false;
        setVisitStatus(NOT_VISITED);
    }


    public Planet getNeighbourOf(CompassPoint direction) {
        if (direction.equals(NORTH)) {
            return north;
        } else if (direction.equals(SOUTH)) {
            return south;
        } else if (direction.equals(EAST)) {
            return east;
        } else {
            return west;
        }
    }

    public void addNeighbour(Planet neighbor, CompassPoint direction) {
        if (direction.equals(NORTH)) {
            this.setNorth(neighbor);
        } else if (direction.equals(SOUTH)) {
            this.setSouth(neighbor);
        } else if (direction.equals(EAST)) {
            this.setEast(neighbor);
        } else if (direction.equals(WEST)) {
            this.setWest(neighbor);
        }
    }

    public void addToUranium(Uranium addedUranium) {
        if (isOrigin())
            throw new ExplorationDroneControlException("Cannot add Uranium to space station");

        this.uranium = this.uranium.addTo(addedUranium);
    }


    public void reduceUranium(Uranium mineQuantity) {
        if (isOrigin())
            throw new ExplorationDroneControlException("Cannot reduce Uranium from space station");
        this.setUranium(mineQuantity.subtractFrom(uranium));
        this.setMined(true);
    }

    public Boolean isVisited() {
        return this.visitStatus == VISITED;
    }

    public void markPlanetVisited() {
        this.setVisitStatus(VISITED);
    }


    public List<Planet> getUnvisitedNeighbours() {
        List<Planet> unvisited = new ArrayList<>();
        if (north != null && !north.isVisited()) unvisited.add(north);
        if (south != null && !south.isVisited()) unvisited.add(south);
        if (east != null && !east.isVisited()) unvisited.add(east);
        if (west != null && !west.isVisited()) unvisited.add(west);
        return unvisited;
    }

    public List<Planet> getVisitedNeighbours() {
        List<Planet> visited = new ArrayList<>();
        if (north != null && north.isVisited()) visited.add(north);
        if (south != null && south.isVisited()) visited.add(south);
        if (east != null && east.isVisited()) visited.add(east);
        if (west != null && west.isVisited()) visited.add(west);
        return visited;
    }


    public Boolean isOrigin() {
        return this.planetType.equals(SPACE_STATION);
    }


    public CompassPoint getDirectionTo(Planet targetPlanet) {

        if (this.equals(targetPlanet)) return null;

        if (this.north != null && this.north.equals(targetPlanet))
            return NORTH;

        if (this.south != null && this.south.equals(targetPlanet))
            return SOUTH;

        if (this.east != null && this.east.equals(targetPlanet))
            return EAST;

        if (this.west != null && this.west.equals(targetPlanet))
            return WEST;

        throw new ExplorationDroneControlException("Cannot determine direction to target planet");
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Planet planet = (Planet) o;
        return Objects.equals(getPlanetId(), planet.getPlanetId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getPlanetId());
    }

}