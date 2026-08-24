package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.mapper;


import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto.HyperspaceEnergyTunnelResponseDto;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnel;
import org.springframework.stereotype.Component;

@Component
public class HyperspaceEnergyTunnelMapper {


    public HyperspaceEnergyTunnelResponseDto toDto(HyperspaceEnergyTunnel tunnel) {
        return new HyperspaceEnergyTunnelResponseDto(
                tunnel.getId().getId(),
                tunnel.getTunnelState(),
                tunnel.getEntryPlanet().getId().getId(),
                tunnel.getExitPlanet().getId().getId());
    }

}
