package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto;

import com.neotee.exploration_drone_controller.domainprimitives.TunnelState;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class HyperspaceEnergyTunnelResponseDto {
    private UUID id;
    private TunnelState tunnelState;
    private UUID entryPlanetId;
    private UUID exitPlanetId;
}
