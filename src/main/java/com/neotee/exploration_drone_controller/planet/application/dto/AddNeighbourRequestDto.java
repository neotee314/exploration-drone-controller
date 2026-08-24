package com.neotee.exploration_drone_controller.planet.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddNeighbourRequestDto(
        @NotNull
        UUID neighbourId,
        @NotNull
        CompassPointRequestDto compassPointDTO
) {
}
