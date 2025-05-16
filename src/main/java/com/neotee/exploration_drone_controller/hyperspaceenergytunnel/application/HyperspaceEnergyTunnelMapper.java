package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application;


import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnel;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.TunnelState;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import com.neotee.exploration_drone_controller.planet.domain.PlanetRepository;
import jakarta.validation.constraints.Negative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class HyperspaceEnergyTunnelMapper {

    @Autowired
    private PlanetRepository planetRepository;

    @Mapping(target = "entryPlanet", source = "entryPlanetId", qualifiedByName = "mapUUIDtoPlanet")
    @Mapping(target = "exitPlanet", source = "exitPlanetId", qualifiedByName = "mapUUIDtoPlanet")
    @Mapping(target = "tunnelState", source = "tunnelState",qualifiedByName = "mapStringToTunnelState")
    public abstract HyperspaceEnergyTunnel toEntity(HyperspaceEnergyTunnelDTO dto);

    @Mapping(target = "entryPlanetId", source = "entryPlanet", qualifiedByName = "mapPlanetToUUID")
    @Mapping(target = "exitPlanetId", source = "exitPlanet", qualifiedByName = "mapPlanetToUUID")
    @Mapping(target = "tunnelState", source = "tunnelState", qualifiedByName = "mapTunnelStateToString")
    public abstract HyperspaceEnergyTunnelDTO toDTO(HyperspaceEnergyTunnel hyperspaceEnergyTunnel);

    @Named("mapUUIDtoPlanet")
    public Planet mapUUIDtoPlanet(UUID planetId){
        if(planetId == null) return null;
        return planetRepository.findById(planetId).orElse(null);
    }

    @Named("mapPlanetToUUID")
    public UUID mapPlanetToUUID(Planet planet){
        if(planet == null) return null;
        return planet.getPlanetId();
    }

    @Named("mapStringToTunnelState")
    public TunnelState mapStringToTunnelState(String tunnelState){
        if(tunnelState == null) return null;
        return TunnelState.valueOf(tunnelState);
    }

    @Named("mapTunnelStateToString")
    public String mapTunnelStateToString(TunnelState tunnelState){
        if(tunnelState == null) return null;
        return tunnelState.toString();
    }

}
