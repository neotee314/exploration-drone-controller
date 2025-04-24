package com.neotee.exploration_drone_controller.planet.domain;



public interface MiningPolicyInterface {
    Boolean canMine(Planet planet, Drone drone);
}
