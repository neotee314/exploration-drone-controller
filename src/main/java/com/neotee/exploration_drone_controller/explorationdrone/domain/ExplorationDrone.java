package com.neotee.exploration_drone_controller.explorationdrone.domain;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPointPath;
import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.planet.domain.Drone;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.datafaker.Faker;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static com.neotee.exploration_drone_controller.explorationdrone.domain.TransportState.NOT_TRANSPORTED;
import static com.neotee.exploration_drone_controller.explorationdrone.domain.TransportState.TRANSPORTED;
import static java.lang.Boolean.TRUE;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ExplorationDrone extends Drone {

    public ExplorationDrone(UUID id) {
        this.id = id;
        this.name = generateCoolName();
        this.load = Load.fromCapacityAndFilling(20, Uranium.fromAmount(0));
        this.transportState = NOT_TRANSPORTED;
        this.commandHistory = CompassPointPath.empty();
    }

    @Override
    public void move(CompassPoint movement) {
        if (movement == null) throw new ExplorationDroneControlException("invalid Data");
        Planet movingPlanet = planet.getNeighbourOf(movement);
        if (movingPlanet == null) throw new ExplorationDroneControlException("No moving against block");
        this.planet.removeDrone(this);
        this.planet = movingPlanet;
        movingPlanet.addDrone(this);
        planet.markPlanetVisited();
        this.commandHistory = this.commandHistory.addMovement(movement);
    }

    @Override
    public void transport() {
        Planet exitPlanet = planet.getExistPlanet();
        this.planet.removeDrone(this);
        this.planet = exitPlanet;
        planet.addDrone(this);
        this.transportState = TRANSPORTED;
    }

    @Override
    public void gohome() {
        if (this.isTransported()) {
            throw new ExplorationDroneControlException("Drone is already transported");
        }

        CompassPointPath path = this.getCommandHistory();
        CompassPoint direction = path.directionToGoBackTo();

        if (direction == null) {
            throw new ExplorationDroneControlException("No direction available to go home.");
        }

        Planet movingPlanet = planet.getNeighbourOf(direction);
        if (movingPlanet == null) {
            throw new ExplorationDroneControlException("No moving against block");
        }

        this.planet.removeDrone(this);
        this.planet = movingPlanet;
        movingPlanet.addDrone(this);
        planet.markPlanetVisited();

        this.setCommandHistory(this.commandHistory.backtrackLastMovement());
    }


    @Override
    public void explore() {
        List<Planet> unvisitedNeighbours = planet.getUnvisitedNeighbours();
        List<Planet> visitedNeighbours = planet.getVisitedNeighbours();

        List<Planet> options = !unvisitedNeighbours.isEmpty() ? unvisitedNeighbours : visitedNeighbours;

        if (options.isEmpty()) {
            throw new ExplorationDroneControlException("All surrounding planets are inaccessible.");
        }

        Planet targetPlanet = options.get(new Random().nextInt(options.size()));
        CompassPoint direction = planet.getDirectionTo(targetPlanet);

        if (direction == null) {
            throw new ExplorationDroneControlException("Direction to target planet could not be determined.");
        }

        Planet movingPlanet = planet.getNeighbourOf(direction);
        if (movingPlanet == null) {
            throw new ExplorationDroneControlException("No moving against block");
        }

        this.planet.removeDrone(this);
        this.planet = movingPlanet;
        movingPlanet.addDrone(this);
        planet.markPlanetVisited();

        this.commandHistory = this.commandHistory.addMovement(direction);
    }


    private String generateCoolName() {
        Faker faker = new Faker();
        return faker.name().firstName() + " " + faker.funnyName().name();
    }

    @Override
    public void addToPlanet(Planet planet) {
        this.planet = planet;
    }

    @Override
    public boolean isTransported() {
        return transportState == TRANSPORTED;
    }

    @Override
    public void mine() {
        if (planet.isMined())
            throw new ExplorationDroneControlException("Planet is already mined");

        if (planet.isOrigin())
            throw new ExplorationDroneControlException("Mining on the origin planet is not allowed");

        if (!planet.canDroneMine(this))
            throw new ExplorationDroneControlException("Drone is not allowed to mine on this planet");

        Uranium available = planet.getUranium();
        Uranium excess = load.leaveBehindWhenFillingFrom(available);
        this.load = load.fillFrom(available);

        Uranium mined = excess.subtractFrom(available);
        planet.reduceUranium(mined);
    }

    @Override
    public Uranium getUranium() {
        return this.getLoad().getUranium();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExplorationDrone drone = (ExplorationDrone) o;
        return Objects.equals(getId(), drone.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
