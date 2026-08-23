package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service;

import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnelRepository;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HyperspaceEnergyTunnelDeletionService {

    private final HyperspaceEnergyTunnelRepository tunnelRepository;

    public void deleteByPlanet(Planet planet) {
        tunnelRepository.deleteByEntryPlanet(planet);
        tunnelRepository.deleteByExitPlanet(planet);
    }
}