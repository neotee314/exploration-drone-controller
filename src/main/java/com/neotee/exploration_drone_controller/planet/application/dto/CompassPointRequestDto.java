package com.neotee.exploration_drone_controller.planet.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompassPointRequestDto(
        @NotNull @NotBlank
        String direction) {
}
