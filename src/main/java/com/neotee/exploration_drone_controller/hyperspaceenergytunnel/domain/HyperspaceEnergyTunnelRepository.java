package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain;

import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HyperspaceEnergyTunnelRepository extends CrudRepository<HyperspaceEnergyTunnel, HyperspaceEnergyTunnelId> {
    @Override
    List<HyperspaceEnergyTunnel> findAll();

    Optional<Planet> findByEntryPlanet(Planet entryPlanet);
}
