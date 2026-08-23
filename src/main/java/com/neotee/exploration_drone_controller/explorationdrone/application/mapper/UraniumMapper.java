package com.neotee.exploration_drone_controller.explorationdrone.application.mapper;

import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.planet.application.dto.UraniumRequestDto;
import org.springframework.stereotype.Component;

@Component
public class UraniumMapper {

    public UraniumRequestDto toDTO(Uranium uranium) {
        if (uranium == null) {
            return null;
        }

        var dto = new UraniumRequestDto();
        dto.setAmount(uranium.getAmount());
        return dto;
    }

}