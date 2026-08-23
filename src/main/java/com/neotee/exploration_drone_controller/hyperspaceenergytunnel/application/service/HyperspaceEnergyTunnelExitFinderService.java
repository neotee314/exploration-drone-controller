package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service;

import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.explorationdrone.application.service.HyperspaceTunnelExitFinder;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnelRepository;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class HyperspaceEnergyTunnelExitFinderService
        implements HyperspaceTunnelExitFinder {

    private final HyperspaceEnergyTunnelRepository hyperspaceEnergyTunnelRepository;

    @Override
    public Planet findByEntryPlanet(Planet entryPlanet) {
        return hyperspaceEnergyTunnelRepository.findByEntryPlanet(entryPlanet)
                .map(tunnel -> tunnel.getExitPlanet())
                .orElseThrow(() -> new DomainValidationException("HyperSpaceEnergyTunnel", "No hyperspace tunnel on this planet"));
    }
}