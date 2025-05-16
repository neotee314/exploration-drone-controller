package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application;

import lombok.Data;

import java.util.UUID;

@Data
public class HyperspaceEnergyTunnelDTO {
    private UUID id;
    private String tunnelState;
    private UUID entryPlanetId;
    private UUID exitPlanetId;
}
