package com.neotee.exploration_drone_controller.domainprimitives;

import lombok.Getter;

@Getter
public enum PlanetType {
    REGULAR("regular"),
    UNKNOWN("unknown"),
    SPACE_STATION("space station");

    private final String value;
    PlanetType(String value) {
        this.value = value;
    }
}
