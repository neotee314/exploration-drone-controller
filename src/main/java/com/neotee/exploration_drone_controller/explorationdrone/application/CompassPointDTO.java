package com.neotee.exploration_drone_controller.explorationdrone.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompassPointDTO {

    private String direction;

    public static CompassPointDTO toDTO(CompassPoint compassPoint) {
        return new CompassPointDTO(compassPoint.toString());
    }

    public CompassPoint toCompassPoint() {
        return CompassPoint.fromString(direction);
    }
}