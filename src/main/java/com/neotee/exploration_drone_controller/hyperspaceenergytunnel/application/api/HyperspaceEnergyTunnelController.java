package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.api;

import com.neotee.exploration_drone_controller.domainprimitives.HyperspaceEnergyTunnelId;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto.HyperspaceEnergyTunnelRequestDto;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto.HyperspaceEnergyTunnelResponseDto;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.mapper.HyperspaceEnergyTunnelMapper;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service.HyperspaceEnergyTunnelApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hyperspaceenergytunnels")
@RequiredArgsConstructor
public class HyperspaceEnergyTunnelController {

    private final HyperspaceEnergyTunnelApplicationService tunnelService;
    private final HyperspaceEnergyTunnelMapper mapper;

    @Operation(summary = "Install tunnel")
    @PostMapping
    public ResponseEntity<HyperspaceEnergyTunnelResponseDto> install(@Valid @RequestBody HyperspaceEnergyTunnelRequestDto dto) {
        var tunnel = tunnelService.install(PlanetId.of(dto.getEntryPlanetId()), PlanetId.of(dto.getExitPlanetId()));

        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{tunnelId}")
                .buildAndExpand(tunnel.getId().value())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toDto(tunnel));
    }

    @Operation(summary = "List all tunnels")
    @GetMapping
    public ResponseEntity<List<HyperspaceEnergyTunnelResponseDto>> findAll() {
        var tunnels = tunnelService.findAll().stream().map(mapper::toDto).toList();
        return ResponseEntity.ok(tunnels);
    }

    @Operation(summary = "Get tunnel details")
    @GetMapping("/{tunnelId}")
    public ResponseEntity<HyperspaceEnergyTunnelResponseDto> findById(@PathVariable UUID tunnelId) {
        var tunnel = tunnelService.findById(HyperspaceEnergyTunnelId.of(tunnelId));
        return ResponseEntity.ok(mapper.toDto(tunnel));
    }

    @Operation(summary = "Shutdown tunnel")
    @DeleteMapping("/{tunnelId}/shutdown")
    public ResponseEntity<Void> shutdown(@PathVariable UUID tunnelId) {
        tunnelService.shutdown(HyperspaceEnergyTunnelId.of(tunnelId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Relocate tunnel")
    @PutMapping("/{tunnelId}/relocate")
    public ResponseEntity<HyperspaceEnergyTunnelResponseDto> relocate(@PathVariable UUID tunnelId, @Valid @RequestBody HyperspaceEnergyTunnelRequestDto dto) {
        var tunnel = tunnelService.relocate(HyperspaceEnergyTunnelId.of(tunnelId), PlanetId.of(dto.getEntryPlanetId()),
                PlanetId.of(dto.getExitPlanetId()));
        return ResponseEntity.ok(mapper.toDto(tunnel));
    }
}