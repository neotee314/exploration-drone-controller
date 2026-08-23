package com.neotee.exploration_drone_controller.explorationdrone.domain.service;


import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;

public interface MiningPolicyServiceInterface {
    Boolean canMine(ExplorationDrone drone);
}
