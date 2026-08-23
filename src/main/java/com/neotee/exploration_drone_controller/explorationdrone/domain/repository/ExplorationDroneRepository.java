package com.neotee.exploration_drone_controller.explorationdrone.domain.repository;

import com.neotee.exploration_drone_controller.domainprimitives.ExplorationDroneId;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExplorationDroneRepository extends CrudRepository<ExplorationDrone, ExplorationDroneId> {

    List<ExplorationDrone> findAll();

    void deleteByPlanet(Planet planet);
}
