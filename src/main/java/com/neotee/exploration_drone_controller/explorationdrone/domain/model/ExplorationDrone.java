package com.neotee.exploration_drone_controller.explorationdrone.domain.model;

import com.neotee.exploration_drone_controller.core.AggregateRoot;
import com.neotee.exploration_drone_controller.domainprimitives.*;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.neotee.exploration_drone_controller.domainprimitives.TransportState.NOT_TRANSPORTED;
import static com.neotee.exploration_drone_controller.domainprimitives.TransportState.TRANSPORTED;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class ExplorationDrone extends AggregateRoot<ExplorationDroneId> {

    @ManyToOne
    private Planet planet;

    @Getter
    private String name;

    @Embedded
    private CompassPointPath path;

    @Enumerated(EnumType.STRING)
    private TransportState transportState;

    @Embedded
    @Getter
    private Load load;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    private List<Command> commandHistory;

    private ExplorationDrone(Planet planet, ExplorationDroneId id) {
        super(id);

        if (planet == null)
            throw new DomainValidationException("ExplorationDrone", "Cannot create a drone on nothing");

        this.planet = planet;
        this.name = generateCoolName();
        this.path = CompassPointPath.empty();
        this.load = Load.fromCapacityAndFilling(20, Uranium.fromAmount(0));
        this.commandHistory = new ArrayList<>();
        this.transportState = NOT_TRANSPORTED;
    }

    public static ExplorationDrone create(Planet planet, ExplorationDroneId id) {
        return new ExplorationDrone(planet, id);
    }

    public void move(CompassPoint direction) {
        var targetPlanet = planet.getNeighbourOf(direction);

        if (targetPlanet == null)
            throw new DomainValidationException("ExplorationDrone", "No planet in this direction");

        this.planet = targetPlanet;
        targetPlanet.markPlanetVisited();

        if (!targetPlanet.isOrigin()) {
            targetPlanet.markPlanetRegular();
        }

        this.path = path.addMovement(direction);
    }

    public void transport(Planet exitPlanet) {
        if (exitPlanet == null)
            throw new DomainValidationException("ExplorationDrone", "Exit planet must not be null");

        this.planet = exitPlanet;
        this.transportState = TRANSPORTED;
    }

    public void gohome() {
        if (isTransported())
            throw new DomainValidationException("ExplorationDrone", "is already transported");

        var direction = path.directionToGoBackTo();

        if (direction == null)
            throw new DomainValidationException("ExplorationDrone", "No direction available to go home.");

        var targetPlanet = planet.getNeighbourOf(direction);

        if (targetPlanet == null)
            throw new DomainValidationException("ExplorationDrone", "No planet in this direction");

        this.planet = targetPlanet;
        targetPlanet.markPlanetVisited();
        this.path = path.backtrackLastMovement();
    }

    public void explore() {
        var unvisitedNeighbours = planet.getUnvisitedNeighbours();
        var visitedNeighbours = planet.getVisitedNeighbours();

        var options = !unvisitedNeighbours.isEmpty()
                ? unvisitedNeighbours
                : visitedNeighbours;

        if (options.isEmpty())
            throw new DomainValidationException("ExplorationDrone", "All surrounding planets are inaccessible.");

        var targetPlanet = options.get(new Random().nextInt(options.size()));

        var direction = planet.getDirectionTo(targetPlanet);

        if (direction == null)
            throw new DomainValidationException("ExplorationDrone", "Direction to target planet could not be determined.");

        move(direction);
    }

    public void mine() {
        if (planet.checkDoubleMine())
            throw new DomainValidationException("ExplorationDrone", "Planet is already mined");

        if (planet.isOrigin())
            throw new DomainValidationException("ExplorationDrone", "Mining on the origin planet is not allowed");

        var available = planet.getUranium();
        var excess = load.leaveBehindWhenFillingFrom(available);

        this.load = load.fillFrom(available);

        var mined = excess.subtractFrom(available);
        planet.reduceUranium(mined);
    }

    public boolean isTransported() {
        return transportState == TRANSPORTED;
    }

    public Uranium getUranium() {
        return load.getUranium();
    }

    public void changeName(String name) {
        if (name == null || name.isBlank())
            throw new DomainValidationException("ExplorationDrone", "Name must not be null or blank");

        this.name = name;
    }

    public void clearCommandHistory() {
        commandHistory.clear();
    }

    public void addCommand(Command command) {
        if (command == null)
            throw new DomainValidationException("ExplorationDrone", "Command must not be null");

        commandHistory.add(command);
    }

    public List<Command> getCommandHistory() {
        return List.copyOf(commandHistory);
    }

    private static String generateCoolName() {
        var faker = new Faker();
        return faker.name().firstName() + " " + faker.funnyName().name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ExplorationDrone drone = (ExplorationDrone) o;
        return Objects.equals(id, drone.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Boolean isPlanetIdEquals(PlanetId planetId) {
        return this.planet.getId() == planetId;
    }

    public PlanetId getPlanetId() {
        return this.planet.getId();
    }
}