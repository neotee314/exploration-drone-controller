package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("/hyperspaceenergytunnel")
@RequiredArgsConstructor
public class HyperSpaceEnergyTunnelController {
    private final HyperspaceEnergyTunnelApplicationService hyperspaceEnergyTunnelApplicationService;

    @Operation(summary = "Install new tunnel")
    @PostMapping
    public ResponseEntity<HyperspaceEnergyTunnelDTO> installHyperspaceEnergyTunnel(
            @RequestBody HyperspaceEnergyTunnelDTO hyperspaceEnergyTunnelDTO) {
        HyperspaceEnergyTunnelDTO dto = hyperspaceEnergyTunnelApplicationService.install(hyperspaceEnergyTunnelDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "List all tunnels")
    @GetMapping
    public ResponseEntity<List<HyperspaceEnergyTunnelDTO>> listAllTunnels() {
        List<HyperspaceEnergyTunnelDTO> tunnels = hyperspaceEnergyTunnelApplicationService.findAll();
        return new ResponseEntity<>(tunnels, HttpStatus.OK);
    }

    @Operation(summary = "Get tunnel details")
    @GetMapping("/{tunnelId}")
    public ResponseEntity<HyperspaceEnergyTunnelDTO> getTunnel(@PathVariable UUID tunnelId) {
        HyperspaceEnergyTunnelDTO dto = hyperspaceEnergyTunnelApplicationService.findById(tunnelId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Shutdown tunnel")
    @DeleteMapping("/{tunnelId}")
    public ResponseEntity<Void> shutdownTunnel(@PathVariable UUID tunnelId) {
        hyperspaceEnergyTunnelApplicationService.shutdown(tunnelId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Relocate tunnel")
    @PutMapping("/{tunnelId}/relocate")
    public ResponseEntity<HyperspaceEnergyTunnelDTO> relocateHyperspaceEnergyTunnel(
            @RequestBody HyperspaceEnergyTunnelDTO hyperspaceEnergyTunnelDTO) {
        HyperspaceEnergyTunnelDTO dto = hyperspaceEnergyTunnelApplicationService.relocate(hyperspaceEnergyTunnelDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}