package com.neotee.exploration_drone_controller.explorationdrone.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommandRequestDto {
    @NotBlank
    private String commandString;
    @NotNull
    private UUID explorationDroneId;
}
