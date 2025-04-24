package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HyperspaceEnergyTunnelRepository extends CrudRepository<HyperspaceEnergyTunnel, UUID> {

}
