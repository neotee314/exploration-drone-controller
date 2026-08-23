package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;

public interface PlanetFinderInterface {
    Planet findPlanetById(PlanetId id);
}
