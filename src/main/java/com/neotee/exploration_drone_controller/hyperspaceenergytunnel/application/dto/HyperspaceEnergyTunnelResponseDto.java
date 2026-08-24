package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto;

import com.neotee.exploration_drone_controller.domainprimitives.TunnelState;

import java.util.UUID;


public record HyperspaceEnergyTunnelResponseDto(
        UUID id,
        TunnelState tunnelState,
        UUID entryPlanetId,
        UUID exitPlanetId) {

}
