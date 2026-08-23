package com.neotee.exploration_drone_controller.planet.application.dto;

import java.util.UUID;


public record PlanetResponseDto(
        UUID planetId,
        UUID northId,
        UUID eastId,
        UUID southId,
        UUID westId,
        String planetType,
        int uranium
) {
}