package com.neotee.exploration_drone_controller.planet.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class AddNeighbourRequestDto {
    @NotNull private UUID neighbourId;
    @NotNull private CompassPointDto compassPointDTO;
}
