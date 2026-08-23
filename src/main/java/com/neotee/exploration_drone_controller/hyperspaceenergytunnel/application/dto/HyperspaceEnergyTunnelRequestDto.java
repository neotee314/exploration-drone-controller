package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class HyperspaceEnergyTunnelRequestDto {
    @NotNull
    private UUID entryPlanetId;
    @NotNull
    private UUID exitPlanetId;
}
