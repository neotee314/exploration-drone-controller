package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.mapper;


import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto.HyperspaceEnergyTunnelResponseDto;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnel;
import org.springframework.stereotype.Component;

@Component
public class HyperspaceEnergyTunnelMapper {


    public HyperspaceEnergyTunnelResponseDto toDto(HyperspaceEnergyTunnel tunnel) {
        return HyperspaceEnergyTunnelResponseDto.builder().
                id(tunnel.getId().getId())
                .tunnelState(tunnel.getTunnelState())
                .entryPlanetId(tunnel.getEntryPlanet().getId().getId())
                .exitPlanetId(tunnel.getExitPlanet().getId().getId())
                .build();
    }

}
