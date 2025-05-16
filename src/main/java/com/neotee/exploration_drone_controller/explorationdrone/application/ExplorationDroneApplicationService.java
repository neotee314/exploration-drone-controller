package com.neotee.exploration_drone_controller.explorationdrone.application;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.PlanetService;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExplorationDroneApplicationService {

    private final ExplorationDroneRepository explorationDroneRepository;
    private final ExplorationDroneMapper droneMapper;
    private final CommandMapper commandMapper;
    private final PlanetService planetService;


    public ExplorationDroneDTO createFromDto(ExplorationDroneDTO request) {

        Planet planet = planetService.getSpaceStation();

        UUID planetId = request.getPlanetId();
        Planet foundPlanet = planetService.findPlanetById(planetId);
        if (foundPlanet == null && planet == null) {
            planet = new Planet();
        } else if (planetService.findPlanetById(planetId) != null) {
            planet = planetService.findPlanetById(planetId);
        }

        planetService.save(planet);

        UUID droneId = request.getId();
        if (droneId == null) droneId = UUID.randomUUID();
        request.setId(droneId);
        ExplorationDrone explorationDrone = droneMapper.toEntity(request);
        explorationDrone.setCommandHistory(new ArrayList<>());
        explorationDroneRepository.save(explorationDrone);

        planet.addDrone(explorationDrone);
        planet.markPlanetVisited();

        explorationDrone.setPlanet(planet);
        explorationDroneRepository.save(explorationDrone);
        planetService.save(planet);
        return droneMapper.toDTO(explorationDrone);
    }

    @Transactional
    public ExplorationDroneDTO sendCommand(UUID droneId, CommandDTO request) {
        if (droneId == null || request == null) throw new ExplorationDroneControlException("droneId is null");
        ExplorationDrone explorationDrone = explorationDroneRepository.findById(droneId)
                .orElseThrow(() -> new ExplorationDroneControlException("Drone not found with id: " + droneId));
        Command command = commandMapper.toEntity(request);
        explorationDrone.sendCommand(command);
        planetService.save(explorationDrone.getPlanet());
        explorationDroneRepository.save(explorationDrone);
        return droneMapper.toDTO(explorationDrone);
    }


    public ExplorationDroneDTO getDroneById(UUID droneId) {
        if (droneId == null) throw new ExplorationDroneControlException("DroneId is null");
        ExplorationDrone drone = explorationDroneRepository.findById(droneId).orElse(null);
        return droneMapper.toDTO(drone);
    }

    public ExplorationDroneDTO changeDroneName(UUID droneId, String name) {
        if (droneId == null) throw new ExplorationDroneControlException("DroneId is null");
        ExplorationDrone explorationDrone = explorationDroneRepository.findById(droneId)
                .orElseThrow(() -> new ExplorationDroneControlException("Drone not found with id: " + droneId));
        explorationDrone.setName(name);
        explorationDroneRepository.save(explorationDrone);
        return droneMapper.toDTO(explorationDrone);
    }


    public List<CommandDTO> getCommandHistory(UUID droneId) {
        ExplorationDrone drone = explorationDroneRepository.findById(droneId)
                .orElseThrow(() -> new ExplorationDroneControlException("Drone not found with id: " + droneId));
        return drone.getCommandHistory().stream().map(commandMapper::toDTO).toList();
    }

    @Transactional
    public void clearCommandHistory(UUID droneId) {
        ExplorationDrone drone = explorationDroneRepository.findById(droneId)
                .orElseThrow(() -> new ExplorationDroneControlException("Drone not found with id: " + droneId));
        drone.getCommandHistory().clear();
        explorationDroneRepository.save(drone);
    }

    public List<ExplorationDroneDTO> getAllDrones() {
        List<ExplorationDroneDTO> result = new ArrayList<>();
        for (ExplorationDrone drone : explorationDroneRepository.findAll()) {
            result.add(droneMapper.toDTO(drone));
        }
        return result;
    }


    public void deleteDrone(UUID droneId) {
        if (droneId == null) throw new ExplorationDroneControlException("DroneId is null");
        explorationDroneRepository.deleteById(droneId);
    }

    public ExplorationDroneDTO spawn(DroneIdDTO droneIdDTO) {
        UUID droneId = UUID.randomUUID();
        if (droneIdDTO != null && droneIdDTO.getDroneId() != null) {
            droneId = droneIdDTO.getDroneId();
        }
        Planet spaceSation = planetService.getSpaceStation();
        if (spaceSation == null) throw new ExplorationDroneControlException("No space station");
        ExplorationDrone drone = explorationDroneRepository.findById(droneId).orElse(null);
        if (drone != null) throw new ExplorationDroneControlException("Drone already exists");
        drone = new ExplorationDrone(droneId);
        drone.setPlanet(spaceSation);
        drone.setCommandHistory(new ArrayList<>());
        explorationDroneRepository.save(drone);
        spaceSation.addDrone(drone);
        return droneMapper.toDTO(drone);
    }
}
