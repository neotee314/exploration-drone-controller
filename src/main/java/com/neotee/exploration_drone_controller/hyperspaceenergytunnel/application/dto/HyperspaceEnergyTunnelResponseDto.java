package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto;

import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.domainprimitives.TunnelState;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class HyperspaceEnergyTunnelResponseDto {
    private HyperspaceEnergyTunnelId id;
    private TunnelState tunnelState;
    private PlanetId entryPlanetId;
    private PlanetId exitPlanetId;
}
