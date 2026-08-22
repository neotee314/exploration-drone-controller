package com.neotee.exploration_drone_controller.planet.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
public class PlanetResponseDto {
    private UUID planetId;
    private UUID northId;
    private UUID eastId;
    private UUID southId;
    private UUID westId;
    private String planetType;
    private UraniumRequestDto uranium;
}
