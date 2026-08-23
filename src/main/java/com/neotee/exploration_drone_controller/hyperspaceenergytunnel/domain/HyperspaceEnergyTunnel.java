package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain;

import com.neotee.exploration_drone_controller.core.AggregateRoot;
import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.domainprimitives.TunnelState;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HyperspaceEnergyTunnel extends AggregateRoot<HyperspaceEnergyTunnelId> {
    @Id
    protected HyperspaceEnergyTunnelId id;

    @OneToOne
    protected Planet entryPlanet;

    @OneToOne
    protected Planet exitPlanet;

    @Enumerated(EnumType.STRING)
    private TunnelState tunnelState;

    private HyperspaceEnergyTunnel(HyperspaceEnergyTunnelId id) {
        this.id = id;
        this.tunnelState = TunnelState.INACTIVE;

    }

    public static HyperspaceEnergyTunnel create(HyperspaceEnergyTunnelId id) {
        return new HyperspaceEnergyTunnel(id);
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


    public void install(Planet entryPlanet, Planet exitPlanet) {
        if (entryPlanet.equals(exitPlanet))
            throw new DomainValidationException("Tunnel", "Entry and exit planet cannot be the same");
        this.setEntryPlanet(entryPlanet);
        this.setExitPlanet(exitPlanet);
        this.activate();
    }


    public Boolean isInActive() {
        return this.tunnelState == TunnelState.INACTIVE;
    }
}
