package com.neotee.exploration_drone_controller.explorationdrone.application;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/explorationDrones")
@RequiredArgsConstructor
public class ExplorationDroneController {

    private final ExplorationDroneApplicationService explorationDroneApplicationService;

    @Operation(summary = "Get all exploration drones")
    @GetMapping
    public ResponseEntity<List<ExplorationDroneDTO>> getAllDrones() {
        return ResponseEntity.ok(explorationDroneApplicationService.getAllDrones());
    }


    @Operation(summary = "spawn a new exploration drone on space station")
    @PostMapping("/spawn")
    public ResponseEntity<ExplorationDroneDTO> spawnDrone(@RequestBody DroneIdDTO droneId) {
        ExplorationDroneDTO explorationDroneDTO = explorationDroneApplicationService.spawn(droneId);
        if (explorationDroneDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(explorationDroneDTO);
    }

    @Operation(summary = "Get a specific exploration drone by ID")
    @GetMapping("/{droneId}")
    public ResponseEntity<ExplorationDroneDTO> getDroneById(@PathVariable UUID droneId) {
        ExplorationDroneDTO explorationDroneDTO = explorationDroneApplicationService.getDroneById(droneId);
        if (explorationDroneDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(explorationDroneDTO);
    }

    @Operation(summary = "Delete a specific exploration drone")
    @DeleteMapping("/{droneId}")
    public ResponseEntity<Void> deleteDrone(@PathVariable UUID droneId) {
        explorationDroneApplicationService.deleteDrone(droneId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change the name of a specific exploration drone")
    @PatchMapping("/{droneId}")
    public ResponseEntity<ExplorationDroneDTO> changeDroneName(
            @PathVariable UUID droneId,
            @RequestBody ExplorationDroneDTO request
    ) {
        ExplorationDroneDTO processdDto = explorationDroneApplicationService.changeDroneName(droneId, request.getName());
        return ResponseEntity.ok(processdDto);
    }


    @Operation(summary = "Give a specific exploration drone a command")
    @PostMapping("/{droneId}/commands")
    public ResponseEntity<ExplorationDroneDTO> sendCommand(@PathVariable UUID droneId, @RequestBody CommandDTO request) {
        ExplorationDroneDTO processedDTO = explorationDroneApplicationService.sendCommand(droneId, request);

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
    public ResponseEntity<List<CommandDTO>> getCommandHistory(@PathVariable UUID droneId) {
        List<CommandDTO> commandHistory = explorationDroneApplicationService.getCommandHistory(droneId);
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
