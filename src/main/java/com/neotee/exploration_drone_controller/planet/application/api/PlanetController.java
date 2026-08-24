package com.neotee.exploration_drone_controller.planet.application.api;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.planet.application.dto.AddNeighbourRequestDto;
import com.neotee.exploration_drone_controller.planet.application.dto.PlanetResponseDto;
import com.neotee.exploration_drone_controller.planet.application.dto.UraniumRequestDto;
import com.neotee.exploration_drone_controller.planet.application.mapper.PlanetMapper;
import com.neotee.exploration_drone_controller.planet.application.service.MapGeneratorService;
import com.neotee.exploration_drone_controller.planet.application.service.MapVisualizationService;
import com.neotee.exploration_drone_controller.planet.application.service.PlanetApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/planets")
@RequiredArgsConstructor
public class PlanetController {

    private final PlanetApplicationService planetApplicationService;
    private final PlanetMapper planetMapper;
    private final MapGeneratorService mapGeneratorService;
    private final MapVisualizationService mapVisualizationService;


    @Operation(summary = "Create a new planet")
    @PostMapping
    public ResponseEntity<PlanetResponseDto> createPlanet() {
        var planet = planetApplicationService.createPlanet();
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(planet.getId().value()).toUri();
        return ResponseEntity.created(location).body(planetMapper.toDto(planet));
    }

    @Operation(summary = "Generate a random planet map")
    @PostMapping("/generate/{planetCount}")
    public ResponseEntity<PlanetResponseDto> generateMap(@PathVariable int planetCount) {
        var spaceStation = mapGeneratorService.generateMap(planetCount);
        return ResponseEntity.ok(planetMapper.toDto(spaceStation));
    }

    @GetMapping(value = "/map", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getMap() {
        var html = mapVisualizationService.generateMapHtml();
        return ResponseEntity.ok(html);
    }

    @Operation(summary = "Add a neighbour to a planet in a given direction")
    @PostMapping("/{planetId}/neighbours")
    public ResponseEntity<PlanetResponseDto> addNeighbour(@PathVariable UUID planetId, @Valid @RequestBody AddNeighbourRequestDto dto) {
        var planet = planetApplicationService.addNeighbour(PlanetId.of(planetId), PlanetId.of(dto.neighbourId()), CompassPoint.fromString(dto.compassPointDTO().direction()));
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(planet.getId().value()).toUri();
        return ResponseEntity.created(location).body(planetMapper.toDto(planet));
    }

    @Operation(summary = "Get all planets")
    @GetMapping
    public ResponseEntity<List<PlanetResponseDto>> getAllPlanets() {
        var response = planetApplicationService.getAllPlanets().stream().map(planetMapper::toDto).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add uranium to a planet")
    @PostMapping("/{planetId}/uraniums")
    public ResponseEntity<PlanetResponseDto> addUranium(@PathVariable UUID planetId, @Valid @RequestBody UraniumRequestDto dto) {
        var planet = planetApplicationService.addUranium(PlanetId.of(planetId), Uranium.fromAmount(dto.amount()));
        return ResponseEntity.ok(planetMapper.toDto(planet));
    }

    @Operation(summary = "Reset all planets")
    @PostMapping("/reset")
    public ResponseEntity<PlanetResponseDto> resetPlanets() {
        var spaceStation = planetApplicationService.resetAll();
        if (spaceStation == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(planetMapper.toDto(spaceStation));
    }
}