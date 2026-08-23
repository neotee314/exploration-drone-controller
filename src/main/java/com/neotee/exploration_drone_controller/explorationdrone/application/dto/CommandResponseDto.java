package com.neotee.exploration_drone_controller.explorationdrone.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommandResponseDto {

    private String commandString;
}
