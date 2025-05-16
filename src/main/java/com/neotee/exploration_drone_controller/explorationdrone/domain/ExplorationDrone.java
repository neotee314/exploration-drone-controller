package com.neotee.exploration_drone_controller.explorationdrone.domain;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.*;
import com.neotee.exploration_drone_controller.planet.domain.Drone;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.datafaker.Faker;

import java.util.*;

import static com.neotee.exploration_drone_controller.explorationdrone.domain.TransportState.NOT_TRANSPORTED;
import static com.neotee.exploration_drone_controller.explorationdrone.domain.TransportState.TRANSPORTED;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class ExplorationDrone extends Drone {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "exploration_drone_command_history",
            joinColumns = @JoinColumn(name = "drone_id")
    )
    private List<Command> commandHistory = new ArrayList<>();

    public ExplorationDrone(UUID id) {
        this.droneId = id;
        this.name = generateCoolName();
        this.load = Load.fromCapacityAndFilling(20, Uranium.fromAmount(0));
        this.transportState = NOT_TRANSPORTED;
        this.path = CompassPointPath.empty();
        this.commandHistory = new ArrayList<>();
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
        this.path = this.path.addMovement(movement);
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

        CompassPointPath path = this.getPath();
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

        this.setPath(this.path.backtrackLastMovement());
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

        this.path = this.path.addMovement(direction);
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


    public void sendCommand(Command command) {
        if (command == null)
            throw new ExplorationDroneControlException("Command cannot be null");
        else if (command.isMove())
            move(command.getMoveDirection());
        else if (command.isExplore())
            explore();
        else if (command.isGohome())
            gohome();
        else if (command.isTransport())
            transport();
        else if (command.isMine())
            mine();
        if (!command.isSpawn()) {
            commandHistory.add(command);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExplorationDrone drone = (ExplorationDrone) o;
        return Objects.equals(getDroneId(), drone.getDroneId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDroneId());
    }
}
