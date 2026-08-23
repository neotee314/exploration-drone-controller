package com.neotee.exploration_drone_controller.domainprimitives;


import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;

import java.util.UUID;

public class PlanetId extends GenericId {


    protected UUID id;

    private PlanetId() {
    }

    public PlanetId(UUID id) {
        super(id);
    }

    public static PlanetId of(UUID value) {
        return new PlanetId(value);
    }

    public static PlanetId newId() {
        return new PlanetId(UUID.randomUUID());
    }

    public static PlanetId of(String id) {
        if (id == null || id.isBlank())
            throw new DomainValidationException("PlanetId", "id must not be null or blank");
        try {
            return new PlanetId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("PlanetId", "must be a valid UUID:");
        }
    }
}
