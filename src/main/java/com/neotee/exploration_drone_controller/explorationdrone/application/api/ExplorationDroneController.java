package com.neotee.exploration_drone_controller.explorationdrone.application.api;

import com.neotee.exploration_drone_controller.domainprimitives.ExplorationDroneId;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandRequestDto;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandResponseDto;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.ExplorationDroneResponseDTO;
import com.neotee.exploration_drone_controller.explorationdrone.application.mapper.CommandMapper;
import com.neotee.exploration_drone_controller.explorationdrone.application.mapper.ExplorationDroneMapper;
import com.neotee.exploration_drone_controller.explorationdrone.application.service.ExplorationDroneApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exploration-drones")
@RequiredArgsConstructor
public class ExplorationDroneController {

    private final ExplorationDroneApplicationService explorationDroneApplicationService;
    private final ExplorationDroneMapper explorationDroneMapper;
    private final CommandMapper commandMapper;

    @Operation(summary = "Get all exploration drones")
    @GetMapping
    public ResponseEntity<List<ExplorationDroneResponseDTO>> getAllDrones() {
        var drones = explorationDroneApplicationService.getAllDrones();
        return ResponseEntity.ok(drones.stream().map(explorationDroneMapper::toDto).toList());
    }

    @Operation(summary = "Spawn a new exploration drone on space station")
    @PostMapping("/spawn")
    public ResponseEntity<ExplorationDroneResponseDTO> spawnDrone() {
        var drone = explorationDroneApplicationService.spawn();
        return ResponseEntity.ok(explorationDroneMapper.toDto(drone));
    }

    @Operation(summary = "Get a specific exploration drone by ID")
    @GetMapping("/{droneId}")
    public ResponseEntity<ExplorationDroneResponseDTO> getDroneById(@PathVariable UUID droneId) {
        var drone = explorationDroneApplicationService.findById(ExplorationDroneId.of(droneId));
        return ResponseEntity.ok(explorationDroneMapper.toDto(drone));
    }

    @Operation(summary = "Delete a specific exploration drone")
    @DeleteMapping("/{droneId}")
    public ResponseEntity<Void> deleteDrone(@PathVariable UUID droneId) {
        explorationDroneApplicationService.delete(ExplorationDroneId.of(droneId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Give a specific exploration drone a command")
    @PostMapping("/{droneId}/commands")
    public ResponseEntity<ExplorationDroneResponseDTO> sendCommand(@PathVariable UUID droneId, @Valid @RequestBody CommandRequestDto request) {
        var command = commandMapper.toCommand(ExplorationDroneId.of(droneId), request);
        var drone = explorationDroneApplicationService.sendCommand(command);
        return ResponseEntity.ok(explorationDroneMapper.toDto(drone));
    }

    @Operation(summary = "List all commands a specific exploration drone has received")
    @GetMapping("/{droneId}/commands")
    public ResponseEntity<List<CommandResponseDto>> getCommandHistory(@PathVariable UUID droneId) {
        var commands = explorationDroneApplicationService.getCommandHistory(ExplorationDroneId.of(droneId));
        return ResponseEntity.ok(commands.stream().map(commandMapper::toDTO).toList());
    }

    @Operation(summary = "Delete the command history of a specific exploration drone")
    @DeleteMapping("/{droneId}/commands")
    public ResponseEntity<Void> clearCommandHistory(@PathVariable UUID droneId) {
        explorationDroneApplicationService.clearCommandHistory(ExplorationDroneId.of(droneId));
        return ResponseEntity.noContent().build();
    }
}