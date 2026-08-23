package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain;

import com.neotee.exploration_drone_controller.core.AggregateRoot;
import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.domainprimitives.TunnelState;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HyperspaceEnergyTunnel extends AggregateRoot<HyperspaceEnergyTunnelId> {

    @OneToOne
    protected Planet entryPlanet;

    @OneToOne
    protected Planet exitPlanet;

    @Enumerated(EnumType.STRING)
    private TunnelState tunnelState;

    private HyperspaceEnergyTunnel(Planet entryPlanet, Planet exitPlanet) {
        this.id = HyperspaceEnergyTunnelId.newId();
        this.activate();

    }

    public void relocate(Planet entryPlanet, Planet exitPlanet) {
        if (this.isInActive()) throw new DomainValidationException("Tunnel", "Tunnel is inactive");
        this.entryPlanet = entryPlanet;
        this.exitPlanet = exitPlanet;
    }


    public void shutdown() {
        if (this.isInActive()) throw new DomainValidationException("Tunnel", "Double shutdown is not possible");
        this.tunnelState = TunnelState.INACTIVE;
    }

    public void activate() {
        this.tunnelState = TunnelState.ACTIVE;
    }


    public static HyperspaceEnergyTunnel install(Planet entryPlanet, Planet exitPlanet) {
        if (entryPlanet.equals(exitPlanet))
            throw new DomainValidationException("Tunnel", "Entry and exit planet cannot be the same");
        return new HyperspaceEnergyTunnel(entryPlanet, exitPlanet);
    }


    public Boolean isInActive() {
        return this.tunnelState == TunnelState.INACTIVE;
    }
}
