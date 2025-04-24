package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnel;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnelRepository;
import com.neotee.exploration_drone_controller.planet.application.PlanetService;
import com.neotee.exploration_drone_controller.planet.application.PlanetValidator;
import com.neotee.exploration_drone_controller.planet.application.TunnelServiceInterface;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class HyperspaceEnergyTunnelService implements TunnelServiceInterface {

    private final HyperspaceEnergyTunnelRepository hyperspaceEnergyTunnelRepository;
    private final PlanetService planetService;
    private final PlanetValidator planetValidator;
    private final TunnelValidator tunnelValidator;


    public HyperspaceEnergyTunnel findById(UUID hypertunnelId) {
        return hyperspaceEnergyTunnelRepository.findById(hypertunnelId).orElse(null);

    }

    @Transactional
    public UUID installHyperspaceEnergyTunnel(UUID entryPlanetId, UUID exitPlanetId) {
        Planet entryPlanet = planetValidator.validatePlanetExists(entryPlanetId);
        Planet exitPlanet = planetValidator.validatePlanetExists(exitPlanetId);

        HyperspaceEnergyTunnel tunnel = new HyperspaceEnergyTunnel();
        tunnel.install(entryPlanet, exitPlanet);

        planetService.save(entryPlanet);
        planetService.save(exitPlanet);
        hyperspaceEnergyTunnelRepository.save(tunnel);
        return tunnel.getId();
    }

    @Transactional
    public void relocateHyperspaceEnergyTunnel(UUID tunnelId, UUID entryPlanetId, UUID exitPlanetId) {
        if (tunnelId == null)
            throw new ExplorationDroneControlException("IDs cannot be null");
        Planet entryPlanet = planetValidator.validatePlanetExists(entryPlanetId);
        Planet exitPlanet = planetValidator.validatePlanetExists(exitPlanetId);

        HyperspaceEnergyTunnel tunnel = hyperspaceEnergyTunnelRepository.findById(tunnelId)
                .orElseThrow(() -> new ExplorationDroneControlException("Tunnel not found"));
        tunnel.relocate(entryPlanet, exitPlanet);
        hyperspaceEnergyTunnelRepository.save(tunnel);
    }

    @Transactional
    public void shutdownHyperspaceEnergyTunnel(UUID tunnelId) {
        if (tunnelId == null)
            throw new ExplorationDroneControlException("tunnel not found");
        HyperspaceEnergyTunnel tunnel = (HyperspaceEnergyTunnel) tunnelValidator.validateTunnelExists(tunnelId);
        tunnel.shutdown();
        hyperspaceEnergyTunnelRepository.save(tunnel);
    }

}
