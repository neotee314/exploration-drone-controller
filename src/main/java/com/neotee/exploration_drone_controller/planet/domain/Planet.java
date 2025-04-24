package com.neotee.exploration_drone_controller.planet.domain;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.explorationdrone.domain.UraniumComparator;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.neotee.exploration_drone_controller.domainprimitives.CompassPoint.*;
import static com.neotee.exploration_drone_controller.planet.domain.PlanetType.SPACE_STATION;
import static com.neotee.exploration_drone_controller.planet.domain.PlanetType.UNKNOWN;
import static com.neotee.exploration_drone_controller.planet.domain.PlanetVisitStatus.NOT_VISITED;
import static com.neotee.exploration_drone_controller.planet.domain.PlanetVisitStatus.VISITED;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Planet {

    @Id
    private UUID id;

    @OneToMany(mappedBy = "planet", fetch = FetchType.LAZY, cascade = CascadeType.MERGE, orphanRemoval = true)
    protected List<Drone> drones = new ArrayList<>();

    @OneToOne(mappedBy = "entryPlanet", cascade = CascadeType.MERGE)
    protected Tunnel hyperTunnel;

    @OneToOne(fetch = FetchType.LAZY)
    protected Planet north;
    @OneToOne(fetch = FetchType.LAZY)
    protected Planet south;
    @OneToOne(fetch = FetchType.LAZY)
    protected Planet west;
    @OneToOne(fetch = FetchType.LAZY)
    protected Planet east;

    protected PlanetType planetType;

    @Embedded
    protected Uranium uranium = Uranium.fromAmount(0);


    @Enumerated(EnumType.STRING)
    private PlanetVisitStatus visitStatus = NOT_VISITED;


    private boolean isMined;


    public Planet() {
        this.id = UUID.randomUUID();
        setPlanetType(UNKNOWN);
        this.isMined= false;
        setVisitStatus(NOT_VISITED);
    }

    public boolean contains(Drone drone) {
        if (drone == null) return false;
        return drones.contains(drone);
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

    public void removeDrone(Drone drone) {
        if (drone == null) throw new ExplorationDroneControlException("Drone cannot be null");
        drones.remove(drone);
    }

    public void addDrone(Drone drone) {
        if (drone == null) throw new ExplorationDroneControlException("Drone cannot be null");
        if (!this.isSpaceStation())
            this.changeToRegular();
        drone.addToPlanet(this);
        this.setVisitStatus(VISITED);
        drones.add(drone);
    }

    public void addNeighbour(Planet neighbor, CompassPoint direction) {
        if (neighbor == null || direction == null)
            throw new ExplorationDroneControlException("Neighbour or Direction cannot be null");
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
        this.uranium = this.uranium.addTo(addedUranium);
    }

    public void addHyperTunnel(Tunnel tunnel) {
        if (tunnel == null) throw new ExplorationDroneControlException("Tunnel cannot be null");
        this.hyperTunnel = tunnel;
    }

    public boolean hasAlreadyHyperTunnel() {
        return hyperTunnel != null;
    }


    public boolean isSpaceStation() {
        return this.planetType.equals(SPACE_STATION);
    }

    public void changeToRegular() {
        setPlanetType(PlanetType.REGULAR);
    }


    public void reduceUranium(Uranium mineQuantity) {
        if (mineQuantity == null) throw new ExplorationDroneControlException("Uranium cannot be null");
        this.setUranium(mineQuantity.subtractFrom(uranium));
        this.setMined(true);
    }

    public Boolean isVisited() {
        return this.visitStatus == VISITED;
    }

    public void markPlanetVisited() {
        this.setVisitStatus(VISITED);
    }

    public List<Planet> getNeighbours() {
        List<Planet> neighbours = new ArrayList<>();
        if (this.north != null) neighbours.add(this.north);
        if (this.south != null) neighbours.add(this.south);
        if (this.east != null) neighbours.add(this.east);
        if (this.west != null) neighbours.add(this.west);
        return neighbours;
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


    public void removeHyperTunnel(Tunnel tunnel) {
        if (tunnel == null) throw new ExplorationDroneControlException("Tunnel cannot be null");
        if (this.hyperTunnel.equals(tunnel)) {
            this.hyperTunnel = null;
        } else {
            throw new ExplorationDroneControlException("Tunnel is not on this planet");
        }
    }

    public CompassPoint getDirectionTo(Planet targetPlanet) {
        if (targetPlanet == null) throw new ExplorationDroneControlException("Target planet cannot be null");

        if (this.equals(targetPlanet)) return null;

        if (this.north != null && this.north.getId().equals(targetPlanet.getId())) {
            return NORTH;
        }
        if (this.south != null && this.south.getId().equals(targetPlanet.getId())) {
            return SOUTH;
        }
        if (this.east != null && this.east.getId().equals(targetPlanet.getId())) {
            return EAST;
        }
        if (this.west != null && this.west.getId().equals(targetPlanet.getId())) {
            return WEST;
        }
        throw new ExplorationDroneControlException("Cannot determine direction to target planet");
    }

    public Planet getExistPlanet() {
        Tunnel tunnel = this.getHyperTunnel();
        if (tunnel == null) throw new ExplorationDroneControlException("Planet has no HyperTunnel");
        return tunnel.getExitPlanet();

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