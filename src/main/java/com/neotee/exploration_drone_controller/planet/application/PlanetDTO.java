package com.neotee.exploration_drone_controller.planet.application;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
public class PlanetDTO {
    private UUID planetId;
    private UUID northId;
    private UUID eastId;
    private UUID southId;
    private UUID westId;
    private String planetType;
    private UraniumDTO uranium;
    private List<UUID> drones;
}
