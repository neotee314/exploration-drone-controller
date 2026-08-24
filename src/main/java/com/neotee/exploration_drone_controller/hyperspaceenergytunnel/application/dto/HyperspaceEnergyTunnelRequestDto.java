package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

public record HyperspaceEnergyTunnelRequestDto(
        @NotNull
        UUID entryPlanetId,
        @NotNull
        UUID exitPlanetId) {
}
