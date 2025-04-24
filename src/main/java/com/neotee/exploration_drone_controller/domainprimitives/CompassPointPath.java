package com.neotee.exploration_drone_controller.domainprimitives;

import certification.ExplorationDroneControlException;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Domain Primitive representing a stack-based path of compass movements for entities like exploration drones.
 * It supports adding movements, backtracking, and querying the direction to return to the origin.
 */
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class CompassPointPath {

    @ElementCollection(fetch = FetchType.EAGER)
    private List<CompassPoint> path = new ArrayList<>();

    private CompassPointPath(List<CompassPoint> path) {
        this.path = new ArrayList<>(path); // defensive copy
    }

    /**
     * Creates and returns a new, empty {@link CompassPointPath}.
     *
     * @return A new empty path.
     */
    public static CompassPointPath empty() {
        return new CompassPointPath();
    }

    /**
     * Adds a movement to the path.
     *
     * @param movement The direction to add.
     * @return A new {@link CompassPointPath} containing the updated movements.
     * @throws ExplorationDroneControlException if movement is null.
     */
    public CompassPointPath addMovement(CompassPoint movement) {
        if (movement == null) {
            throw new ExplorationDroneControlException("Cannot add a null movement to path");
        }
        List<CompassPoint> newPath = new ArrayList<>(this.path);
        newPath.add(movement);
        return new CompassPointPath(newPath);
    }

    /**
     * Gets the direction needed to go back from the most recent movement.
     *
     * @return The opposite {@link CompassPoint} of the last movement.
     * @throws ExplorationDroneControlException if the path is empty.
     */
    public CompassPoint directionToGoBackTo() {
        if (path.isEmpty()) {
            throw new ExplorationDroneControlException("Path is empty — no movement to go back to");
        }
        return path.get(path.size() - 1).oppositeDirection();
    }

    /**
     * Removes the last movement and returns a new path without it.
     *
     * @return A new {@link CompassPointPath} with one fewer movement.
     * @throws ExplorationDroneControlException if the path is already empty.
     */
    public CompassPointPath backtrackLastMovement() {
        if (path.isEmpty()) {
            throw new ExplorationDroneControlException("Cannot backtrack — path is already empty");
        }
        List<CompassPoint> newPath = new ArrayList<>(this.path);
        newPath.remove(newPath.size() - 1);
        return new CompassPointPath(newPath);
    }

    /**
     * Gets the number of movements in the path.
     *
     * @return The path size.
     */
    public int length() {
        return path.size();
    }

    /**
     * Returns an unmodifiable copy of the current movement path.
     *
     * @return List of {@link CompassPoint}s in the path.
     */
    public List<CompassPoint> getPath() {
        return Collections.unmodifiableList(path);
    }
}
