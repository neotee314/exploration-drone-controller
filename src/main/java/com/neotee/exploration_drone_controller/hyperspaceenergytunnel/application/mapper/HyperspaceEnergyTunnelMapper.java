package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.mapper;


import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto.HyperspaceEnergyTunnelRequestDto;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto.HyperspaceEnergyTunnelResponseDto;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnel;

public class HyperspaceEnergyTunnelMapper {


    public HyperspaceEnergyTunnelResponseDto toDto(HyperspaceEnergyTunnel tunnel) {
        return HyperspaceEnergyTunnelResponseDto.builder().
                id(tunnel.getId())
                .tunnelState(tunnel.getTunnelState())
                .entryPlanetId(tunnel.getEntryPlanet().getId())
                .exitPlanetId(tunnel.getExitPlanet().getId())
                .build();
    }

//    public HyperspaceEnergyTunnel toEntity(HyperspaceEnergyTunnelRequestDto dto){
//
//        return HyperspaceEnergyTunnel.create(dto.getId(),dto.getEntryPlanetId(),dto.getExitPlanetId());
//
//    }
}
