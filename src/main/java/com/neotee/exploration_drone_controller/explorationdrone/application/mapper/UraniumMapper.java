package com.neotee.exploration_drone_controller.explorationdrone.application.mapper;


import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.planet.application.dto.UraniumRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UraniumMapper {


    default UraniumRequestDto toDTO(Uranium uranium) {
        if (uranium == null) return null;
        UraniumRequestDto dto = new UraniumRequestDto();
        dto.setAmount(uranium.getAmount());
        return dto;
    }

    default Uranium toEntity(UraniumRequestDto uraniumRequestDto) {
        if (uraniumRequestDto == null) return null;
        return Uranium.fromAmount(uraniumRequestDto.getAmount());
    }
}
