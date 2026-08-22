package com.neotee.exploration_drone_controller.planet.application.api;

import com.neotee.exploration_drone_controller.exceptions.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.planet.application.mapper.PlanetMapper;
import com.neotee.exploration_drone_controller.planet.application.service.PlanetApplicationService;
import com.neotee.exploration_drone_controller.planet.application.dto.AddNeighbourDTO;
import com.neotee.exploration_drone_controller.planet.application.dto.PlanetResponseDto;
import com.neotee.exploration_drone_controller.planet.application.dto.UraniumRequestDto;
import com.neotee.exploration_drone_controller.planet.domain.repository.PlanetRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/planets")
@RequiredArgsConstructor
public class PlanetController {

    private final PlanetApplicationService planetApplicationService;
    private final PlanetRepository planetRepository;
    private final PlanetMapper planetMapper;

    @PostMapping
    public ResponseEntity<PlanetResponseDto> createPlanet() {
        var createdPlanet = planetApplicationService.createPlanet();
        var response = planetMapper.toDTO(createdPlanet);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Add a neighbour to a planet in a given direction")
    @PostMapping("/{planetId}/neighbours")
    public ResponseEntity<Void> addNeighbour(@PathVariable UUID planetId, @Valid @RequestBody AddNeighbourDTO dto) {
        var planet = planetRepository.findById(planetId).orElseThrow(() -> new ExplorationDroneControlException("Planet not found"));
        var neighbour = planetRepository.findById(dto.getNeighbourId()).orElseThrow(() -> new ExplorationDroneControlException("Neighbour not found"));

        var direction = CompassPoint.fromString(dto.getCompassPointDTO().getDirection());

        planetApplicationService.addNeighbour(planet, neighbour, direction);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all planets")
    @GetMapping
    public ResponseEntity<List<PlanetResponseDto>> getAllPlanets() {
        var planets = planetApplicationService.getAllPlanets();
        var response = planets.stream().map(planetMapper::toDTO).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add uranium to a planet")
    @PostMapping("/{planetId}/uranium")
    public ResponseEntity<PlanetResponseDto> addUranium(@PathVariable UUID planetId, @Valid @RequestBody UraniumRequestDto dto) {
        var planet = planetRepository.findById(planetId).orElseThrow(() -> new ExplorationDroneControlException("Planet not found"));
        var uranium = Uranium.fromAmount(dto.getAmount());
        var updatedPlanet = planetApplicationService.addUranium(planet, uranium);

        return ResponseEntity.ok(planetMapper.toDTO(updatedPlanet));
    }
}
