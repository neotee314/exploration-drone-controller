package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import com.neotee.exploration_drone_controller.domainprimitives.*;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.explorationdrone.domain.model.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.repository.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.service.DroneDeletionService;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExplorationDroneApplicationService {

    private final ExplorationDroneRepository explorationDroneRepository;
    private final SpawnService spawnService;
    private final MovementService movementService;
    private final MiningService miningService;


    public ExplorationDrone sendCommand(Command command) {
        var droneUuid = command.getDroneId();
        var droneId = ExplorationDroneId.of(droneUuid);
        if (command.isSpawn())
            return spawnService.spawnById(droneId);
        var drone = explorationDroneRepository.findById(droneId).orElseThrow(() -> new DomainValidationException("ExplorationDroneApplicationService", "Drone does not exist"));
        if (command.isMove()) {
            movementService.move(drone, command.getMoveDirection());
        } else if (command.isExplore()) {
            movementService.explore(drone);
        } else if (command.isGohome()) {
            movementService.gohome(drone);
        } else if (command.isTransport()) {
            movementService.transport(drone);
        } else if (command.isMine()) {
            miningService.mine(drone);
        }

        drone.addCommand(command);

        return explorationDroneRepository.save(drone);
    }

    public ExplorationDrone findById(ExplorationDroneId id) {
        return explorationDroneRepository.findById(id)
                .orElseThrow(() -> new DomainValidationException("ExplorationDroneApplicationService", "Drone not found with id: " + id));
    }


    public List<Command> getCommandHistory(ExplorationDroneId droneId) {
        var drone = findById(droneId);
        return drone.getCommandHistory();
    }


    public void clearCommandHistory(ExplorationDroneId droneId) {
        var drone = findById(droneId);
        drone.getCommandHistory().clear();
        explorationDroneRepository.save(drone);
    }

    public List<ExplorationDrone> getAllDrones() {
        return new ArrayList<>(explorationDroneRepository.findAll());
    }


    public Load getExplorationDroneLoad(ExplorationDroneId droneId) {
        var drone = findById(droneId);
        return drone.getLoad();
    }

    public Planet getPlanetWhichDroneIsOn(ExplorationDroneId droneId) {
        var drone = findById(droneId);
        return drone.getPlanet();

    }

    public List<ExplorationDrone> getAllDronesOnPlanet(PlanetId planetId) {
        return explorationDroneRepository.findAll().stream()
                .filter(drone -> drone.getPlanet().getId().equals(planetId))
                .toList();
    }

    public ExplorationDrone spawn() {
        return spawnService.spawn();
    }

    public void delete(ExplorationDroneId id) {
        explorationDroneRepository.deleteById(id);
    }

}
