package com.neotee.exploration_drone_controller.planet.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UraniumRequestDto {
    @Min(1)
    @Max(20)
    private Integer amount;
}
