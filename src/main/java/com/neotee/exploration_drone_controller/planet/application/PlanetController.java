package com.neotee.exploration_drone_controller.planet.application;

import com.neotee.exploration_drone_controller.explorationdrone.application.CompassPointDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/planets")
@RequiredArgsConstructor
public class PlanetController {

    private final PlanetApplicationService planetApplicationService;

    @Operation(summary = "Create a new planet")
    @PostMapping
    public ResponseEntity<PlanetDTO> createPlanet(@RequestBody PlanetIdDTO planetId) {
        PlanetDTO createdPlanet = planetApplicationService.createPlanet(planetId);
        URI returnURI = URI.create("/planets/" + createdPlanet.getPlanetId());
        return ResponseEntity.created(returnURI).body(createdPlanet);
    }

    @Operation(summary = "Add a neighbour to a planet in a given direction")
    @PostMapping("/{planetId}/neighbours")
    public ResponseEntity<Void> addNeighbour(
            @RequestBody AddNeighbourDTO addNeighbourDTO
    ) {
        planetApplicationService.addNeighbour(addNeighbourDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all planets")
    @GetMapping
    public ResponseEntity<List<PlanetDTO>> getAllPlanets() {
        List<PlanetDTO> planets = planetApplicationService.getAllPlanets();
        return ResponseEntity.ok(planets);
    }

    @Operation(summary = "Add uranium to a planet")
    @PostMapping("/uranium")
    public ResponseEntity<PlanetDTO> addUranium(@RequestBody PlanetIdDTO planetId, @RequestBody UraniumDTO uranium) {
        PlanetDTO planetDTO = planetApplicationService.addUranium(planetId,uranium);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{planet-id}")
                .buildAndExpand(planetDTO.getPlanetId())
                .toUri();
        return ResponseEntity
                .created(returnURI)
                .body(planetDTO);
    }
}
