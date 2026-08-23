package com.neotee.exploration_drone_controller.planet.application.service;

import com.neotee.exploration_drone_controller.planet.domain.model.Planet;

public interface DroneDeletionService {
    void deleteByPlanet(Planet planet);
}