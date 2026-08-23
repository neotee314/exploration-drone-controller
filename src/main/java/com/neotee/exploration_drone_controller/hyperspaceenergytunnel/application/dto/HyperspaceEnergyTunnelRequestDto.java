package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto;

import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class HyperspaceEnergyTunnelRequestDto {
    @NotNull
    private PlanetId entryPlanetId;
    @NotNull
    private PlanetId exitPlanetId;
}
