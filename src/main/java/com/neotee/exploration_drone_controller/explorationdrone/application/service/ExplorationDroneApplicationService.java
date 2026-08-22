package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import com.neotee.exploration_drone_controller.exceptions.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.explorationdrone.application.mapper.ExplorationDroneMapper;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandRequestDto;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.ExplorationDroneResponseDTO;
import com.neotee.exploration_drone_controller.explorationdrone.application.mapper.CommandMapper;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.service.PlanetApplicationService;
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
    private final PlanetApplicationService planetService;


    public ExplorationDrone sendCommand(ExplorationDrone drone, Command command) {
        drone.sendCommand(command);
        return explorationDroneRepository.save(drone);
    }


    public ExplorationDroneResponseDTO getDroneById(UUID droneId) {
        if (droneId == null) throw new ExplorationDroneControlException("DroneId is null");
        ExplorationDrone drone = explorationDroneRepository.findById(droneId).orElse(null);
        return droneMapper.toDTO(drone);
    }

    public ExplorationDroneResponseDTO changeDroneName(UUID droneId, String name) {
        if (droneId == null) throw new ExplorationDroneControlException("DroneId is null");
        ExplorationDrone explorationDrone = explorationDroneRepository.findById(droneId)
                .orElseThrow(() -> new ExplorationDroneControlException("Drone not found with id: " + droneId));
        explorationDrone.setName(name);
        explorationDroneRepository.save(explorationDrone);
        return droneMapper.toDTO(explorationDrone);
    }


    public List<CommandRequestDto> getCommandHistory(UUID droneId) {
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

    public List<ExplorationDrone> getAllDrones() {
        List<ExplorationDrone> result = new ArrayList<>();
        explorationDroneRepository.findAll().forEach(result::add);
        return result;
    }

    public ExplorationDrone spawn() {
        UUID droneId = UUID.randomUUID();
        var spaceSation = planetService.getSpaceStation();
        var drone = ExplorationDrone.of(spaceSation, droneId);
        return explorationDroneRepository.save(drone);
    }
}
