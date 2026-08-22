package com.neotee.exploration_drone_controller.explorationdrone.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompassPointDTO {

    @NotNull @NotBlank
    private String direction;
}