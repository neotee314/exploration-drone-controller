package com.neotee.exploration_drone_controller.domainprimitives;

import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;
@Embeddable
public class HyperspaceEnergyTunnelId extends GenericId {

    protected HyperspaceEnergyTunnelId() {
    }

    public HyperspaceEnergyTunnelId(UUID id) {
        super(id);
    }

    public static HyperspaceEnergyTunnelId of(UUID value) {
        return new HyperspaceEnergyTunnelId(value);
    }

    public static HyperspaceEnergyTunnelId newId() {
        return new HyperspaceEnergyTunnelId(UUID.randomUUID());
    }

    public static HyperspaceEnergyTunnelId of(String id) {
        if (id == null || id.isBlank()) {
            throw new DomainValidationException(
                    "HyperspaceEnergyTunnelId",
                    "id must not be null or blank"
            );
        }

        try {
            return new HyperspaceEnergyTunnelId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException(
                    "HyperspaceEnergyTunnelId",
                    "must be a valid UUID"
            );
        }
    }
}