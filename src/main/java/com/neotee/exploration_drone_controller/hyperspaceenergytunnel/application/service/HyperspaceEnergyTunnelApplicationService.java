package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnel;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HyperspaceEnergyTunnelApplicationService {
    private final HyperspaceEnergyTunnelRepository hyperspaceEnergyTunnelRepository;
    private final PlanetFinderInterface planetFinderInterface;

    public HyperspaceEnergyTunnel install(HyperspaceEnergyTunnelId tunnelId, PlanetId entryPlanetid, PlanetId exitPlanetId) {
        var tunnel = hyperspaceEnergyTunnelRepository.findById(tunnelId).orElseThrow(() -> new DomainValidationException("Tunnel", "Tunnel does not exist"));
        var entryPlanet = planetFinderInterface.findPlanetById(entryPlanetid);
        var exitPlanet = planetFinderInterface.findPlanetById(exitPlanetId);
        tunnel.install(entryPlanet, exitPlanet);
        return hyperspaceEnergyTunnelRepository.save(tunnel);
    }



    public void shutdown(HyperspaceEnergyTunnelId tunnelId) {
        var tunnel = hyperspaceEnergyTunnelRepository.findById(tunnelId).orElseThrow(() -> new DomainValidationException("Tunnel", "Tunnel does not exist"));
        tunnel.shutdown();
        hyperspaceEnergyTunnelRepository.save(tunnel);
    }


    public HyperspaceEnergyTunnel relocate(HyperspaceEnergyTunnelId tunnelId, PlanetId entryPlanetid, PlanetId exitPlanetId) {
        var tunnel = hyperspaceEnergyTunnelRepository.findById(tunnelId).orElseThrow(() -> new DomainValidationException("Tunnel", "Tunnel does not exist"));
        var entryPlanet = planetFinderInterface.findPlanetById(entryPlanetid);
        var exitPlanet = planetFinderInterface.findPlanetById(exitPlanetId);
        tunnel.relocate(entryPlanet,exitPlanet);
        return hyperspaceEnergyTunnelRepository.save(tunnel);
    }

    public HyperspaceEnergyTunnel create(HyperspaceEnergyTunnelId id) {
        var tunnel = HyperspaceEnergyTunnel.create(id);
        return hyperspaceEnergyTunnelRepository.save(tunnel);
    }

    public List<HyperspaceEnergyTunnel> findAll() {
        return hyperspaceEnergyTunnelRepository.findAll();
    }

    public HyperspaceEnergyTunnel findById(HyperspaceEnergyTunnelId id) {
        return hyperspaceEnergyTunnelRepository.findById(id).orElseThrow(() -> new DomainValidationException("Tunnel", "Tunnel does not exist"));

    }
}
