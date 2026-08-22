package com.neotee.exploration_drone_controller.planet.domain.repository;

import com.neotee.exploration_drone_controller.planet.domain.model.Tunnel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TunnelRepository extends CrudRepository<Tunnel, UUID> {
}
