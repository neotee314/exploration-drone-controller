package com.neotee.exploration_drone_controller.anticorruption;

import com.neotee.exploration_drone_controller.certification.HyperspaceEnergyTunnelUseCases;
import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service.HyperspaceEnergyTunnelApplicationService;
import com.neotee.exploration_drone_controller.planet.application.service.PlanetApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MyFantasticHyperspaceEnergyTunnelUseCasesService implements HyperspaceEnergyTunnelUseCases {
    private final HyperspaceEnergyTunnelApplicationService hyperspaceEnergyTunnelApplicationService;
    @Override
    public UUID installHyperspaceEnergyTunnel(UUID entryPlanetId, UUID exitPlanetId) {
        var entryPlanetID = PlanetId.of(entryPlanetId);
        var exitPlanetID  = PlanetId.of(exitPlanetId);
        return hyperspaceEnergyTunnelApplicationService.install(entryPlanetID, exitPlanetID).getId().getId();
    }

    @Override
    public void relocateHyperspaceEnergyTunnel(UUID tunnelId, UUID entryPlanetId, UUID exitPlanetId) {

        var entryPlanetID = PlanetId.of(entryPlanetId);
        var exitPlanetID  = PlanetId.of(exitPlanetId);
        var tunnelID = HyperspaceEnergyTunnelId.of(tunnelId);
        hyperspaceEnergyTunnelApplicationService.relocate(tunnelID, entryPlanetID, exitPlanetID);

    }

    @Override
    public void shutdownHyperspaceEnergyTunnel(UUID tunnelId) {
        var tunnelID = HyperspaceEnergyTunnelId.of(tunnelId);
        hyperspaceEnergyTunnelApplicationService.shutdown(tunnelID);
    }
}
