package com.neotee.exploration_drone_controller.planet.domain.service;


import com.neotee.exploration_drone_controller.planet.domain.model.Drone;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;

public interface MiningPolicyInterface {
    Boolean canMine(Planet planet, Drone drone);
}
