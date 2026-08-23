package com.neotee.exploration_drone_controller.planet.application.api;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.planet.application.dto.AddNeighbourDTO;
import com.neotee.exploration_drone_controller.planet.application.dto.PlanetResponseDto;
import com.neotee.exploration_drone_controller.planet.application.dto.UraniumRequestDto;
import com.neotee.exploration_drone_controller.planet.application.mapper.PlanetMapper;
import com.neotee.exploration_drone_controller.planet.application.service.PlanetApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/planets")
@RequiredArgsConstructor
public class PlanetController {

    private final PlanetApplicationService planetApplicationService;
    private final PlanetMapper planetMapper;


    @Operation(summary = "Create a new planet")
    @PostMapping
    public ResponseEntity<PlanetResponseDto> createPlanet() {

        var planet = planetApplicationService.createPlanet();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(planet.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(planetMapper.toDTO(planet));
    }


    @Operation(summary = "Add a neighbour to a planet in a given direction")
    @PostMapping("/{planetId}/neighbours")
    public ResponseEntity<Void> addNeighbour(
            @PathVariable UUID planetId,
            @Valid @RequestBody AddNeighbourDTO dto
    ) {

        var planet = planetApplicationService.findPlanetById(planetId);

        var neighbour = planetApplicationService.findPlanetById(
                dto.getNeighbourId()
        );

        var direction = CompassPoint.fromString(
                dto.getCompassPointDTO().getDirection()
        );

        planetApplicationService.addNeighbour(
                planet,
                neighbour,
                direction
        );

        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Get all planets")
    @GetMapping
    public ResponseEntity<List<PlanetResponseDto>> getAllPlanets() {

        var response = planetApplicationService
                .getAllPlanets()
                .stream()
                .map(planetMapper::toDTO)
                .toList();

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Add uranium to a planet")
    @PostMapping("/{planetId}/uranium")
    public ResponseEntity<PlanetResponseDto> addUranium(
            @PathVariable UUID planetId,
            @Valid @RequestBody UraniumRequestDto dto
    ) {

        var planet = planetApplicationService.findPlanetById(planetId);

        var uranium = Uranium.fromAmount(dto.getAmount());

        var updatedPlanet = planetApplicationService.addUranium(
                planet,
                uranium
        );

        return ResponseEntity.ok(
                planetMapper.toDTO(updatedPlanet)
        );
    }
}