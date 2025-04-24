package com.neotee.exploration_drone_controller.explorationdrone.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExplorationDroneRepository extends CrudRepository<ExplorationDrone, UUID> {
}
