package com.neotee.exploration_drone_controller.explorationdrone.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommandDTO {
    private String commandString;
    private UUID explorationDroneId;
}
