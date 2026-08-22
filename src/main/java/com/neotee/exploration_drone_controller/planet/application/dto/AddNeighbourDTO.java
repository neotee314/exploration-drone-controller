package com.neotee.exploration_drone_controller.planet.application.dto;

import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CompassPointDTO;
import lombok.Data;

import java.util.UUID;
@Data
public class AddNeighbourDTO {
    private UUID planetId;
    private UUID neighbourId;
    private CompassPointDTO compassPointDTO;
}
