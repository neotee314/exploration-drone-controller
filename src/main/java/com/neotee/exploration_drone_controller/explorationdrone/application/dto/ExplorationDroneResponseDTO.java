package com.neotee.exploration_drone_controller.explorationdrone.application.dto;

import com.neotee.exploration_drone_controller.domainprimitives.ExplorationDroneId;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Builder
public class ExplorationDroneResponseDTO {
    private String name;
    private UUID id;
    private UUID planetId;
    private List<CommandResponseDto> commandHistory;
}
