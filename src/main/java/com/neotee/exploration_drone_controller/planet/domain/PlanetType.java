package com.neotee.exploration_drone_controller.planet.domain;

import lombok.Getter;

@Getter
public enum PlanetType {
    REGULAR("regular"),
    UNKNOWN("unknown"),
    SPACE_STATION("space station"),
    HABITABLE("habitable"),
    EXPLORABLE("explorable");

    private final String value;

    // Constructor for enums that take a string value
    PlanetType(String value) {
        this.value = value;
    }
}
