package com.neotee.exploration_drone_controller.planet.domain.model;

import com.neotee.exploration_drone_controller.core.AggregateRoot;
import com.neotee.exploration_drone_controller.domainprimitives.*;
import com.neotee.exploration_drone_controller.exceptions.ExplorationDroneControlException;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Planet extends AggregateRoot<PlanetId> {

    protected String name;

    @OneToOne
    @JoinColumn(name = "north_planet_id")
    protected Planet north;

    @OneToOne
    @JoinColumn(name = "south_planet_id")
    protected Planet south;

    @OneToOne
    @JoinColumn(name = "west_planet_id")
    protected Planet west;

    @OneToOne
    @JoinColumn(name = "east_planet_id")
    protected Planet east;

    protected PlanetType planetType;

    @Embedded
    protected Uranium uranium;


    @Enumerated(EnumType.STRING)
    private PlanetVisitStatus visitStatus;


    private Boolean isMined;

    protected Planet(PlanetId planetId) {
        this.id = planetId;
        this.uranium = Uranium.fromAmount(0);
        setPlanetType(UNKNOWN);
        this.isMined = false;
        setVisitStatus(NOT_VISITED);
    }

    public static Planet create() {
        var planetId = PlanetId.newId();
        return new Planet(planetId);
    }

    public static Planet create(PlanetId planetId) {
        return new Planet(planetId);
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

    public void addNeighbour(Planet neighbour, CompassPoint direction) {
        switch (direction) {

            case NORTH -> {
                this.north = neighbour;
                neighbour.south = this;
            }

            case SOUTH -> {
                this.south = neighbour;
                neighbour.north = this;
            }

            case EAST -> {
                this.east = neighbour;
                neighbour.west = this;
            }

            case WEST -> {
                this.west = neighbour;
                neighbour.east = this;
            }
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
        this.setIsMined(true);
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

    public void removeNeighbours() {
        this.north = null;
        this.south = null;
        this.east = null;
        this.west = null;
    }


    public Boolean checkDoubleMine() {
        return isMined;
    }

    public void markPlanetRegular() {
        this.setPlanetType(PlanetType.REGULAR);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Planet planet = (Planet) o;
        return Objects.equals(getId(), planet.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }


}