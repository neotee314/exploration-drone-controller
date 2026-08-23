package com.neotee.exploration_drone_controller.explorationdrone.domain.model;

import com.neotee.exploration_drone_controller.core.AggregateRoot;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.exceptions.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.*;
import com.neotee.exploration_drone_controller.domainprimitives.TransportState;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.datafaker.Faker;

import java.util.*;

import static com.neotee.exploration_drone_controller.domainprimitives.TransportState.NOT_TRANSPORTED;
import static com.neotee.exploration_drone_controller.domainprimitives.TransportState.TRANSPORTED;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class ExplorationDrone extends AggregateRoot<ExplorationDroneId> {

    @Id
    protected ExplorationDroneId id;

    protected String name;

    @Embedded
    protected CompassPointPath path;

    @ManyToOne
    protected Planet planet;

    @Enumerated(EnumType.STRING)
    protected TransportState transportState;

    @Embedded
    protected Load load;

    @ElementCollection
    @CollectionTable
    private List<Command> commandHistory;

    private ExplorationDrone(Planet planet, ExplorationDroneId id) {
        this.id = id;
        this.planet = planet;
        this.name = generateCoolName();
        this.load = Load.fromCapacityAndFilling(20, Uranium.fromAmount(0));
        this.transportState = NOT_TRANSPORTED;
        this.path = CompassPointPath.empty();
        this.commandHistory = new ArrayList<>();
    }

    public static ExplorationDrone create(Planet planet, ExplorationDroneId id) {
        if (planet == null) throw new ExplorationDroneControlException("cannot create a Drone on nothing");
        return new ExplorationDrone(planet, id);

    }


    public void move(CompassPoint movement) {
        var movingPlanet = planet.getNeighbourOf(movement);
        if (movingPlanet == null) throw new ExplorationDroneControlException("No moving against block");
        this.planet = movingPlanet;
        planet.markPlanetVisited();
        this.path = this.path.addMovement(movement);
    }


    public void transport(Planet exitPlanet) {
        this.planet = exitPlanet;
        this.transportState = TRANSPORTED;
    }


    public void gohome() {
        if (this.isTransported())
            throw new DomainValidationException("Drone", "is already transported");


        var path = this.getPath();
        var direction = path.directionToGoBackTo();

        if (direction == null) {
            throw new DomainValidationException("Drone", "No direction available to go home.");
        }

        this.planet = planet.getNeighbourOf(direction);
        planet.markPlanetVisited();
        this.setPath(this.path.backtrackLastMovement());
    }


    public void explore() {
        var unvisitedNeighbours = planet.getUnvisitedNeighbours();
        var visitedNeighbours = planet.getVisitedNeighbours();

        var options = !unvisitedNeighbours.isEmpty() ? unvisitedNeighbours : visitedNeighbours;

        if (options.isEmpty()) {
            throw new DomainValidationException("Drone", "All surrounding planets are inaccessible.");
        }

        var targetPlanet = options.get(new Random().nextInt(options.size()));
        var direction = planet.getDirectionTo(targetPlanet);

        if (direction == null)
            throw new DomainValidationException("Drone", "Direction to target planet could not be determined.");

        var movingPlanet = planet.getNeighbourOf(direction);
        if (movingPlanet == null)
            throw new DomainValidationException("Drone", "No moving against block");

        this.planet = movingPlanet;
        planet.markPlanetVisited();

        this.path = this.path.addMovement(direction);
    }


    private String generateCoolName() {
        Faker faker = new Faker();
        return faker.name().firstName() + " " + faker.funnyName().name();
    }


    public boolean isTransported() {
        return transportState == TRANSPORTED;
    }


    public void mine() {
        if (planet.isMined())
            throw new DomainValidationException("Drone", "Planet is already mined");

        if (planet.isOrigin())
            throw new DomainValidationException("Drone", "Mining on the origin planet is not allowed");

        var available = planet.getUranium();
        var excess = load.leaveBehindWhenFillingFrom(available);
        this.load = load.fillFrom(available);

        var mined = excess.subtractFrom(available);
        planet.reduceUranium(mined);
    }


    public Uranium getUranium() {
        return this.getLoad().getUranium();
    }


    public void sendCommand(Command command) {
        if (command.isMove())
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
        return Objects.equals(getId(), drone.getId());
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
