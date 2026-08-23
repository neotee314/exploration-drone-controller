package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.explorationdrone.application.service.HyperTunnelInterface;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnel;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnelRepository;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class HyperspaceEnergyTunnelService implements HyperTunnelInterface {

    private final HyperspaceEnergyTunnelRepository hyperspaceEnergyTunnelRepository;


    public HyperspaceEnergyTunnel findById(HyperspaceEnergyTunnelId hypertunnelId) {
        return hyperspaceEnergyTunnelRepository.findById(hypertunnelId).orElse(null);

    }


    public HyperspaceEnergyTunnelId installHyperspaceEnergyTunnel(Planet entryPlanet, Planet exitPlanet) {
        var tunnel = new HyperspaceEnergyTunnel();
        tunnel.install(entryPlanet, exitPlanet);
        hyperspaceEnergyTunnelRepository.save(tunnel);
        return tunnel.getId();
    }


    public void relocateHyperspaceEnergyTunnel(HyperspaceEnergyTunnel tunnel, Planet entryPlanet, Planet exitPlanet) {
        tunnel.relocate(entryPlanet, exitPlanet);
        hyperspaceEnergyTunnelRepository.save(tunnel);
    }


    public void shutdownHyperspaceEnergyTunnel(HyperspaceEnergyTunnel tunnel) {
        tunnel.shutdown();
        hyperspaceEnergyTunnelRepository.save(tunnel);
    }

    @Override
    public Planet findByEntryPlanet(Planet entryPlanet) {
        return hyperspaceEnergyTunnelRepository.findByEntryPlanet(entryPlanet)
                .orElseThrow(() -> new DomainValidationException("HyperSpaceEnergyTunnel", "No hyperspace tunnel on this planet"));
    }
}
