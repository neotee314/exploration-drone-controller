package com.neotee.exploration_drone_controller.explorationdrone.application;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final ExplorationDroneService explorationDroneService;

    @Operation(summary = "Get all exploration drones")
    @GetMapping
    public ResponseEntity<List<ExplorationDroneDTO>> getAllDrones() {
        return ResponseEntity.ok(explorationDroneService.getAllDrones());
    }

    @Operation(summary = "Create a new exploration drone")
    @PostMapping
    public ResponseEntity<ExplorationDroneDTO> createDrone(@RequestBody ExplorationDroneDTO request) {
        ExplorationDroneDTO explorationDroneDTO = explorationDroneService.createFromDto(request);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{explorationDrone-id}")
                .buildAndExpand(explorationDroneDTO.getId())
                .toUri();
        return ResponseEntity
                .created(returnURI)
                .body(explorationDroneDTO);
    }

    @Operation(summary = "Get a specific exploration drone by ID")
    @GetMapping("/{droneId}")
    public ResponseEntity<ExplorationDroneDTO> getDroneById(@PathVariable UUID droneId) {
        ExplorationDroneDTO explorationDroneDTO = explorationDroneService.getDroneById(droneId);
        if (explorationDroneDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(explorationDroneDTO);
    }

    @Operation(summary = "Delete a specific exploration drone")
    @DeleteMapping("/{droneId}")
    public ResponseEntity<Void> deleteDrone(@PathVariable UUID droneId) {
        explorationDroneService.deleteDrone(droneId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change the name of a specific exploration drone")
    @PatchMapping("/{droneId}")
    public ResponseEntity<ExplorationDroneDTO> changeDroneName(@PathVariable UUID droneId,
                                                @RequestBody ExplorationDroneDTO request) {
        ExplorationDroneDTO processdDto = explorationDroneService.changeDroneName(droneId, request.getName());
        return ResponseEntity.ok(processdDto);
    }

    @Operation(summary = "Give a specific exploration drone a commandhistory")
    @PostMapping("/{droneId}/commands/")
    public ResponseEntity<ExplorationDroneDTO> sendCommandHistory(@PathVariable UUID droneId, @RequestBody CommandDTO request) {
        ExplorationDroneDTO processedDTO = explorationDroneService.addCommandHistory(droneId, request);

        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(processedDTO.getId())
                .toUri();
        return ResponseEntity
                .created(returnURI)
                .body(processedDTO);
    }

    @Operation(summary = "Give a specific exploration drone a command")
    @PostMapping("/{droneId}/commands")
    public ResponseEntity<ExplorationDroneDTO> sendCommand(@PathVariable UUID droneId, @RequestBody CommandDTO request) {
        ExplorationDroneDTO processedDTO = explorationDroneService.sendCommand(droneId, request);

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
        List<CommandDTO> commandHistory = explorationDroneService.getCommandHistory(droneId);
        if (commandHistory == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(commandHistory);
    }


    @Operation(summary = "Delete the command history of a specific exploration drone")
    @DeleteMapping("/{droneId}/commands")
    public ResponseEntity clearCommandHistory(@PathVariable UUID droneId) {
        explorationDroneService.clearCommandHistory(droneId);
        return new ResponseEntity(OK);
    }
}
