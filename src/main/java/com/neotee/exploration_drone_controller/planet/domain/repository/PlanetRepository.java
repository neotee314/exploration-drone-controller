package com.neotee.exploration_drone_controller.planet.domain.repository;

import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetType;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanetRepository extends CrudRepository<Planet, PlanetId> {

    @Override
    List<Planet> findAll();

    void deleteByPlanetTypeNot(PlanetType planetType);

    Optional<Planet> findByPlanetType(PlanetType planetType);

    Optional<Planet> findFirstByPlanetType(PlanetType planetType);
}
