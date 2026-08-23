package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import com.neotee.exploration_drone_controller.planet.domain.model.Planet;



public interface HyperTunnelInterface {
    Planet findByEntryPlanet(Planet entryPlanet);
}
