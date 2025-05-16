package com.neotee.exploration_drone_controller.planet.application;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.explorationdrone.application.CompassPointDTO;
import com.neotee.exploration_drone_controller.explorationdrone.application.UraniumMapper;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import com.neotee.exploration_drone_controller.planet.domain.PlanetRepository;
import com.neotee.exploration_drone_controller.planet.domain.PlanetType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.neotee.exploration_drone_controller.planet.domain.PlanetType.UNKNOWN;

@Service
@RequiredArgsConstructor
public class PlanetApplicationService {
    private final PlanetRepository planetRepository;
    private final PlanetMapper planetMapper;
    private final UraniumMapper uraniumMapper;

    public PlanetDTO createPlanet(PlanetIdDTO planetIdDTO) {
        UUID planetId = UUID.randomUUID();
        if (planetIdDTO != null && planetIdDTO.getPlanetId() != null) {
            planetId = planetIdDTO.getPlanetId();
        }

        List<Planet> planets = planetRepository.findAll();
        if (planets.isEmpty()) {
            Planet planet = new Planet();
            planet.setPlanetId(planetId);
            planet.setPlanetType(PlanetType.SPACE_STATION);
            planetRepository.save(planet);
            return planetMapper.toDTO(planet);
        }
        if (planetRepository.findById(planetId).isPresent())
            throw new ExplorationDroneControlException("Planet already exists");

        Planet planet = new Planet();
        planet.setPlanetId(planetId);
        planet.setPlanetType(UNKNOWN);
        planetRepository.save(planet);
        return planetMapper.toDTO(planet);

    }

    @Transactional
    public void addNeighbour(AddNeighbourDTO addNeighbourDTO) {
        UUID neighbourId = UUID.randomUUID();
        if (addNeighbourDTO == null || addNeighbourDTO.getPlanetId() == null ||
                addNeighbourDTO.getCompassPointDTO() == null)
            throw new ExplorationDroneControlException("planetId and neighbour must not be null");
        if( addNeighbourDTO.getNeighbourId() != null){
            neighbourId = addNeighbourDTO.getNeighbourId();
        }
        UUID planetId = addNeighbourDTO.getPlanetId();
        CompassPointDTO compassPointDTO = addNeighbourDTO.getCompassPointDTO();
        Planet planet = planetRepository.findById(planetId).orElseThrow(() -> new ExplorationDroneControlException("Planet not found"));
        Planet neighbor = planetRepository.findById(neighbourId).orElse(null);
        if (neighbor == null) {
            neighbor = new Planet();
            neighbor.setPlanetId(neighbourId);
            neighbor.setPlanetType(UNKNOWN);
            planetRepository.save(neighbor);
        }
        CompassPoint compassPoint = CompassPoint.fromString(compassPointDTO.getDirection().toString());
        planet.addNeighbour(neighbor, compassPoint);
        neighbor.addNeighbour(planet, compassPoint.oppositeDirection());
        planetRepository.save(neighbor);
        planetRepository.save(planet);
    }

    public List<PlanetDTO> getAllPlanets() {
        List<Planet> planets = planetRepository.findAll();
        if (planets.isEmpty()) return new ArrayList<>();
        List<PlanetDTO> planetDTOs = new ArrayList<>();
        for (Planet planet : planets) {
            planetDTOs.add(planetMapper.toDTO(planet));
        }
        return planetDTOs;
    }

    public PlanetDTO addUranium(PlanetIdDTO planetId, UraniumDTO uraniumDTO) {
        if (planetId == null || planetId.getPlanetId() == null || uraniumDTO == null)
            throw new ExplorationDroneControlException("planetId and neighbour must not be null");
        Planet planet = planetRepository.findById(planetId.getPlanetId()).orElseThrow(() -> new ExplorationDroneControlException("Planet not found"));
        if (planet.isSpaceStation()) throw new ExplorationDroneControlException("cannot add Uranium to space station");
        Uranium addedUranium = uraniumMapper.toEntity(uraniumDTO);
        planet.addToUranium(addedUranium);
        planetRepository.save(planet);
        return planetMapper.toDTO(planet);
    }
}
