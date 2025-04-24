package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnelRepository;
import com.neotee.exploration_drone_controller.planet.domain.Tunnel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TunnelValidator {

    private final HyperspaceEnergyTunnelRepository hyperspaceEnergyTunnelRepository;

    public Tunnel validateTunnelExists(UUID tunnelId) {
        if (tunnelId == null) throw new ExplorationDroneControlException("TunnelId is null");
        return hyperspaceEnergyTunnelRepository.findById(tunnelId).orElseThrow(
                () -> new ExplorationDroneControlException("Tunnel not found")
        );
    }

}
