package com.neotee.exploration_drone_controller.planet.application;

import com.neotee.exploration_drone_controller.planet.domain.Tunnel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TunnelServiceInterface {
    Tunnel findById(UUID tunnelId);

}
