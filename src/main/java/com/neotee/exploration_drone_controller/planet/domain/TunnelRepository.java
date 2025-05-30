package com.neotee.exploration_drone_controller.planet.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TunnelRepository extends CrudRepository<Tunnel, UUID> {
}
