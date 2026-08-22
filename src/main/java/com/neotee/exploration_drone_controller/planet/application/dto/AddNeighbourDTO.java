package com.neotee.exploration_drone_controller.planet.application.dto;

import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CompassPointDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;
@Data
public class AddNeighbourDTO {
    @NotNull private UUID neighbourId;
    @NotNull private CompassPointDTO compassPointDTO;
}
