package com.neotee.exploration_drone_controller.explorationdrone.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExplorationDroneDTO {
    private String name;
    private UUID id;
    private UUID planetId;
    private List<CommandDTO> commandHistory;
}
