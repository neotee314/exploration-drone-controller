package com.neotee.exploration_drone_controller.explorationdrone.application;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDrone;
import com.neotee.exploration_drone_controller.explorationdrone.domain.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.DroneServiceInterface;
import com.neotee.exploration_drone_controller.planet.application.PlanetService;
import com.neotee.exploration_drone_controller.planet.domain.Drone;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ExplorationDroneService implements DroneServiceInterface {

    private final ExplorationDroneRepository explorationDroneRepository;
    private final DroneValidator droneValidator;
    private final ExplorationDroneMapper droneMapper;
    private final CommandMapper commandMapper;
    private final PlanetService planetService;


    public void deleteAll() {
        explorationDroneRepository.deleteAll();
    }

    public Load getExplorationDroneLoad(UUID droneId) {
        ExplorationDrone explorationDrone = droneValidator.validateDroneExists(droneId);
        return explorationDrone.getLoad();

    }


    @Override
    public Drone findDroneById(UUID droneId) {
        return droneValidator.validateDroneExists(droneId);
    }

    public UUID getDronePlanet(UUID droneId) {
        ExplorationDrone explorationDrone = droneValidator.validateDroneExists(droneId);
        return explorationDrone.getPlanet().getPlanetId();
    }

    @Override
    public void save(Drone drone) {
        explorationDroneRepository.save((ExplorationDrone) drone);
    }

    @Override
    public void validateSpawn(UUID droneId) {
        droneValidator.validateSpawn(droneId);
    }

    @Override
    public Drone validateDroneExists(UUID droneId) {
        return droneValidator.validateDroneExists(droneId);
    }

    @Override
    public List<ExplorationDroneDTO> getAllDrones() {
        Iterable<ExplorationDrone> drones = explorationDroneRepository.findAll();
        return StreamSupport.stream(drones.spliterator(), false)
                .map(droneMapper::toDTO)
                .toList();
    }

    public ExplorationDroneDTO createFromDto(ExplorationDroneDTO request) {
        ExplorationDrone explorationDrone = droneMapper.toEntity(request);
        Planet spaceStation = planetService.getSpaceStation();
        spaceStation.addDrone(explorationDrone);
        spaceStation.markPlanetVisited();

        if (request.getId() == null) explorationDrone.setDroneId(UUID.randomUUID());

        List<Command> commandHistory = new ArrayList<>();
        if (request.getCommandHistory() != null && !request.getCommandHistory().isEmpty()) {
            for (CommandDTO commandDTO : request.getCommandHistory()) {
                Command command = commandMapper.toEntity(commandDTO);
                commandHistory.add(command);
            }
        }

        explorationDrone.setCommandHistory(commandHistory);
        explorationDroneRepository.save(explorationDrone);
        planetService.save(spaceStation);

        return droneMapper.toDTO(explorationDrone);
    }



    public ExplorationDroneDTO getDroneById(UUID droneId) {
        if (droneId == null) throw new ExplorationDroneControlException("DroneId is null");
        ExplorationDrone drone = explorationDroneRepository.findById(droneId).orElse(null);
        return droneMapper.toDTO(drone);
    }

    public void deleteDrone(UUID droneId) {
        if (droneId == null) throw new ExplorationDroneControlException("DroneId is null");
        explorationDroneRepository.deleteById(droneId);
    }

    public ExplorationDroneDTO changeDroneName(UUID droneId, String name) {
        if (droneId == null) throw new ExplorationDroneControlException("DroneId is null");
        ExplorationDrone explorationDrone = explorationDroneRepository.findById(droneId)
                .orElseThrow(() -> new ExplorationDroneControlException("Drone not found with id: " + droneId));
        explorationDrone.setName(name);
        explorationDroneRepository.save(explorationDrone);
        return droneMapper.toDTO(explorationDrone);
    }

    @Transactional
    public ExplorationDroneDTO addCommandHistory(UUID droneId, CommandDTO commandDTO) {
        if (droneId == null || commandDTO == null) throw new ExplorationDroneControlException("droneId is null");
        ExplorationDrone explorationDrone = explorationDroneRepository.findById(droneId)
                .orElseThrow(() -> new ExplorationDroneControlException("Drone not found with id: " + droneId));
        Command command = commandMapper.toEntity(commandDTO);
        //Command command = Command.fromCommandString("[" + commandDTO.getCommandString() + "," + commandDTO.getExplorationDroneId() + "]");
        explorationDrone.addCommandHistory(command);

        explorationDroneRepository.save(explorationDrone);
        return droneMapper.toDTO(explorationDrone);
    }

    public List<CommandDTO> getCommandHistory(UUID droneId) {
        ExplorationDrone drone = explorationDroneRepository.findById(droneId)
                .orElseThrow(() -> new ExplorationDroneControlException("Drone not found with id: " + droneId));

        return drone.getCommandHistory()
                .stream()
                .map(commandMapper::toDTO)
                .toList();
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

    @Transactional
    public void clearCommandHistory(UUID droneId) {
        ExplorationDrone drone = explorationDroneRepository.findById(droneId)
                .orElseThrow(() -> new ExplorationDroneControlException("Drone not found with id: " + droneId));
        drone.getCommandHistory().clear();
        explorationDroneRepository.save(drone);
    }

    public void spawn(UUID droneId) {
        droneValidator.validateSpawn(droneId);
        ExplorationDrone explorationDrone = new ExplorationDrone(droneId);

        Planet spaceStation = planetService.getSpaceStation();
        spaceStation.addDrone(explorationDrone);
        spaceStation.markPlanetVisited();
        explorationDroneRepository.save(explorationDrone);
        planetService.save(spaceStation);
    }


}
