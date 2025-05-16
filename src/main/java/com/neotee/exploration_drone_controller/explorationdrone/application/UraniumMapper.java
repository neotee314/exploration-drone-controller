package com.neotee.exploration_drone_controller.explorationdrone.application;


import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.planet.application.UraniumDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UraniumMapper {


    default UraniumDTO toDTO(Uranium uranium) {
        if (uranium == null) return null;
        UraniumDTO dto = new UraniumDTO();
        dto.setAmount(uranium.getAmount());
        return dto;
    }

    default Uranium toEntity(UraniumDTO uraniumDTO) {
        if (uraniumDTO == null) return null;
        return Uranium.fromAmount(uraniumDTO.getAmount());
    }
}
