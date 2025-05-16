package com.neotee.exploration_drone_controller.planet.application;

import com.neotee.exploration_drone_controller.explorationdrone.application.CompassPointDTO;
import lombok.Data;

import java.util.UUID;
@Data
public class AddNeighbourDTO {
    private UUID planetId;
    private UUID neighbourId;
    private CompassPointDTO compassPointDTO;
}
