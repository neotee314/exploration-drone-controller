package com.neotee.exploration_drone_controller.explorationdrone.domain.repository;

import com.neotee.exploration_drone_controller.domainprimitives.ExplorationDroneId;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExplorationDroneRepository extends CrudRepository<ExplorationDrone, ExplorationDroneId> {
}
