package com.neotee.exploration_drone_controller.explorationdrone.application.api;

import com.neotee.exploration_drone_controller.exceptions.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandRequestDto;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.ExplorationDroneResponseDTO;
import com.neotee.exploration_drone_controller.explorationdrone.application.mapper.CommandMapper;
import com.neotee.exploration_drone_controller.explorationdrone.application.mapper.ExplorationDroneMapper;
import com.neotee.exploration_drone_controller.explorationdrone.application.service.ExplorationDroneApplicationService;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDroneRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/explorationDrones")
@RequiredArgsConstructor
public class ExplorationDroneController {

    private final ExplorationDroneApplicationService explorationDroneApplicationService;
    private final ExplorationDroneRepository explorationDroneRepository;
    private final ExplorationDroneMapper explorationDroneMapper;
    private final CommandMapper commandMapper;

    @Operation(summary = "Get all exploration drones")
    @GetMapping
    public ResponseEntity<List<ExplorationDroneResponseDTO>> getAllDrones() {
        var drones = explorationDroneApplicationService.getAllDrones();
        var result = drones.stream().map(explorationDroneMapper::toDTO).toList();
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "spawn a new exploration drone on space station")
    @PostMapping("/spawn")
    public ResponseEntity<ExplorationDroneResponseDTO> spawnDrone() {
        var drone = explorationDroneApplicationService.spawn();
        return ResponseEntity.ok(explorationDroneMapper.toDTO(drone));
    }

    @Operation(summary = "Get a specific exploration drone by ID")
    @GetMapping("/{droneId}")
    public ResponseEntity<ExplorationDroneResponseDTO> getDroneById(@PathVariable UUID droneId) {
        var drone = explorationDroneRepository.findById(droneId).orElse(null);
        if (drone == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(explorationDroneMapper.toDTO(drone));
    }

    @Operation(summary = "Delete a specific exploration drone")
    @DeleteMapping("/{droneId}")
    public ResponseEntity<Void> deleteDrone(@PathVariable UUID droneId) {
        explorationDroneRepository.deleteById(droneId);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Give a specific exploration drone a command")
    @PostMapping("/{droneId}/commands")
    public ResponseEntity<ExplorationDroneResponseDTO> sendCommand(@PathVariable UUID droneId, @Valid @RequestBody CommandRequestDto request) {
        var drone = explorationDroneRepository.findById(droneId).orElseThrow(() -> new ExplorationDroneControlException("No Drone found"));
        var command = commandMapper.toCommand(request);
        var processedDTO = explorationDroneApplicationService.sendCommand(drone, command);

        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(processedDTO.getId())
                .toUri();
        return ResponseEntity
                .created(returnURI)
                .body(processedDTO);
    }

    @Operation(summary = "List all the commands a specific exploration drone has received so far")
    @GetMapping("/{droneId}/commands")
    public ResponseEntity<List<CommandRequestDto>> getCommandHistory(@PathVariable UUID droneId) {
        List<CommandRequestDto> commandHistory = explorationDroneApplicationService.getCommandHistory(droneId);
        if (commandHistory == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(commandHistory);
    }


    @Operation(summary = "Delete the command history of a specific exploration drone")
    @DeleteMapping("/{droneId}/commands")
    public ResponseEntity clearCommandHistory(@PathVariable UUID droneId) {
        explorationDroneApplicationService.clearCommandHistory(droneId);
        return new ResponseEntity(OK);
    }
}
