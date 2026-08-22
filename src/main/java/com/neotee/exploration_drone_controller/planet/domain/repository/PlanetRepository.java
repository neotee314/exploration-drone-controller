package com.neotee.exploration_drone_controller.planet.domain.repository;

import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlanetRepository extends CrudRepository<Planet, UUID> {

    @Override
    List<Planet> findAll();

}
