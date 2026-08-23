package com.neotee.exploration_drone_controller.planet.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PlanetResponseDto {

    private UUID planetId;
    private UUID northId;
    private UUID eastId;
    private UUID southId;
    private UUID westId;
    private String planetType;
    private Integer uranium;
}