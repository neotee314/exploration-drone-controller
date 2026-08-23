package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnel;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnelRepository;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HyperspaceEnergyTunnelApplicationService {

    private final HyperspaceEnergyTunnelRepository tunnelRepository;
    private final PlanetFinderInterface planetFinder;

    public HyperspaceEnergyTunnel install(PlanetId entryPlanetId, PlanetId exitPlanetId) {
        var entryPlanet = planetFinder.findPlanetById(entryPlanetId);
        if (tunnelRepository.existsByEntryPlanet(entryPlanet)) {
            throw new DomainValidationException("HyperspaceEnergyTunnel", "A hyperspace energy tunnel already starts at this planet");
        }
        var exitPlanet = planetFinder.findPlanetById(exitPlanetId);
        var tunnel = HyperspaceEnergyTunnel.install(entryPlanet, exitPlanet);
        return tunnelRepository.save(tunnel);
    }

    public void shutdown(HyperspaceEnergyTunnelId tunnelId) {
        var tunnel = findById(tunnelId);
        tunnel.shutdown();
        tunnelRepository.save(tunnel);
    }

    public HyperspaceEnergyTunnel relocate(HyperspaceEnergyTunnelId tunnelId, PlanetId entryPlanetId, PlanetId exitPlanetId) {
        var tunnel = findById(tunnelId);
        if (tunnel.isInActive())
            throw new DomainValidationException("HyperspaceEnergyTunnelApplicationService", "Validation erro for shutdown relocation");
        if (entryPlanetId.equals(exitPlanetId))
            throw new DomainValidationException("HyperspaceEnergyTunnelApplicationService", "Entry and exit planet must be different");

        var entryPlanet = planetFinder.findPlanetById(entryPlanetId);
        var exitPlanet = planetFinder.findPlanetById(exitPlanetId);
        tunnel.relocate(entryPlanet, exitPlanet);
        return tunnelRepository.save(tunnel);
    }

    public List<HyperspaceEnergyTunnel> findAll() {
        return tunnelRepository.findAll();
    }

    public HyperspaceEnergyTunnel findById(HyperspaceEnergyTunnelId tunnelId) {
        return tunnelRepository.findById(tunnelId)
                .orElseThrow(() -> new DomainValidationException("Tunnel", "Tunnel does not exist"));
    }
}