package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.api;

import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto.HyperspaceEnergyTunnelRequestDto;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto.HyperspaceEnergyTunnelResponseDto;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.mapper.HyperspaceEnergyTunnelMapper;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service.HyperspaceEnergyTunnelApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController("/api/v1/hyperspaceenergytunnels")
@RequiredArgsConstructor
public class HyperSpaceEnergyTunnelController {
    private final HyperspaceEnergyTunnelApplicationService hyperspaceEnergyTunnelApplicationService;
    private final HyperspaceEnergyTunnelMapper mapper;


    @PostMapping("/{tunnelId}")
    public ResponseEntity<HyperspaceEnergyTunnelResponseDto> create(@PathVariable UUID tunnelId) {
        var id = HyperspaceEnergyTunnelId.of(tunnelId);
        var created = hyperspaceEnergyTunnelApplicationService.create(id);
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("{tunnelId}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.toDto(created));
    }

    @Operation(summary = "Install tunnel")
    @PostMapping("/{tunnelId}")
    public ResponseEntity<HyperspaceEnergyTunnelResponseDto> installHyperspaceEnergyTunnel(
            @PathVariable UUID tunnelId, @Valid @RequestBody HyperspaceEnergyTunnelRequestDto dto) {

        var id = HyperspaceEnergyTunnelId.of(tunnelId);

        var installedTunnel = hyperspaceEnergyTunnelApplicationService.install(id, dto.getEntryPlanetId(), dto.getExitPlanetId());
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{tunnelId}")
                .buildAndExpand(installedTunnel.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toDto(installedTunnel));
    }

    @Operation(summary = "List all tunnels")
    @GetMapping
    public ResponseEntity<List<HyperspaceEnergyTunnelResponseDto>> listAllTunnels() {
        var tunnels = hyperspaceEnergyTunnelApplicationService.findAll();
        return ResponseEntity.ok(tunnels.stream().map(mapper::toDto).toList());
    }

    @Operation(summary = "Get tunnel details")
    @GetMapping("/{tunnelId}")
    public ResponseEntity<HyperspaceEnergyTunnelResponseDto> getTunnel(@PathVariable UUID tunnelId) {
        var tunnel = hyperspaceEnergyTunnelApplicationService.findById(HyperspaceEnergyTunnelId.of(tunnelId));
        return ResponseEntity.ok(mapper.toDto(tunnel));
    }

    @Operation(summary = "Shutdown tunnel")
    @DeleteMapping("/{tunnelId}/shutdown")
    public ResponseEntity<Void> shutdownTunnel(@PathVariable UUID tunnelId) {
        var id = HyperspaceEnergyTunnelId.of(tunnelId);
        hyperspaceEnergyTunnelApplicationService.shutdown(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Relocate tunnel")
    @PutMapping("/{tunnelId}/relocate")
    public ResponseEntity<HyperspaceEnergyTunnelResponseDto> relocateHyperspaceEnergyTunnel(
            @PathVariable UUID tunnelId, @RequestBody HyperspaceEnergyTunnelRequestDto dto) {
        var id = HyperspaceEnergyTunnelId.of(tunnelId);
        var relocatedTunnel = hyperspaceEnergyTunnelApplicationService.relocate(id, dto.getEntryPlanetId(), dto.getExitPlanetId());
        return ResponseEntity.ok(mapper.toDto(relocatedTunnel));
    }
}