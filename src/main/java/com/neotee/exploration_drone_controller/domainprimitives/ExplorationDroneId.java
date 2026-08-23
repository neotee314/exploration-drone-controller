package com.neotee.exploration_drone_controller.domainprimitives;

import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ExplorationDroneId extends GenericId {

    protected ExplorationDroneId() {
    }

    public ExplorationDroneId(UUID id) {
        super(id);
    }

    public static ExplorationDroneId of(UUID value) {
        return new ExplorationDroneId(value);
    }

    public static ExplorationDroneId newId() {
        return new ExplorationDroneId(UUID.randomUUID());
    }

    public static ExplorationDroneId of(String id) {
        if (id == null || id.isBlank()) {
            throw new DomainValidationException(
                    "ExplorationDroneId",
                    "id must not be null or blank"
            );
        }

        try {
            return new ExplorationDroneId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException(
                    "ExplorationDroneId",
                    "must be a valid UUID"
            );
        }
    }
}