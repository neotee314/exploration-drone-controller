package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import com.neotee.exploration_drone_controller.planet.domain.Tunnel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HyperspaceEnergyTunnel extends Tunnel {



    @Enumerated(EnumType.STRING)
    private TunnelState tunnelState;

    public void relocate(Planet entryPlanet, Planet exitPlanet) {
        if (entryPlanet == null || exitPlanet == null || entryPlanet.equals(exitPlanet))
            throw new ExplorationDroneControlException("entryPlanet and exitPlanet must not be null");

        if (this.isInActive()) throw new ExplorationDroneControlException("Tunnel is inactive");

        this.entryPlanet.removeHyperTunnel(this);

        entryPlanet.addHyperTunnel(this);

        this.entryPlanet = entryPlanet;
        this.exitPlanet = exitPlanet;
    }

    @Override
    public void shutdown() {
        if (this.isInActive()) throw new ExplorationDroneControlException("Double shutdown is not possible");
        this.tunnelState = TunnelState.INACTIVE;
    }

    public void activate() {
        this.tunnelState = TunnelState.ACTIVE;
    }


    public void install(Planet entryPlanet, Planet exitPlanet) {
        if (entryPlanet == null || exitPlanet == null)
            throw new ExplorationDroneControlException("entryPlanet and exitPlanet must not be null");

        if (entryPlanet.equals(exitPlanet))
            throw new ExplorationDroneControlException("Entry and exit planet cannot be the same");

        if (entryPlanet.hasAlreadyHyperTunnel())
            throw new ExplorationDroneControlException("Entry planet already has a hyperspace energy tunnel");

        entryPlanet.addHyperTunnel(this);

        this.setEntryPlanet(entryPlanet);
        this.setExitPlanet(exitPlanet);
        this.activate();
    }

    @Override
    public Boolean isInActive() {
        return this.tunnelState == TunnelState.INACTIVE;
    }

    @Override
    public Planet getExitPlanet() {
        return this.exitPlanet;
    }
}
