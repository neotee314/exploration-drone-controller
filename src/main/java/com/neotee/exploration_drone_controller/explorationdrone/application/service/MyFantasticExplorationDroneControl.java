package com.neotee.exploration_drone_controller.explorationdrone.application.service;

import certification.ExplorationDroneControl;
import com.neotee.exploration_drone_controller.domainprimitives.ExplorationDroneId;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import certification.HyperspaceEnergyTunnelUseCases;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.explorationdrone.domain.repository.ExplorationDroneRepository;
import com.neotee.exploration_drone_controller.planet.application.service.PlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ComponentScan("com.neotee.exploration_drone_controller")
public class MyFantasticExplorationDroneControl implements ExplorationDroneControl {

    private final SpawnService spawnService;
    private final ExplorationDroneRepository explorationDroneRepository;
    private final PlanetService planetService;
    private final HyperTunnelInterface hyperTunnelInterface;
    private final MovementService movementService;
    private final MiningService miningService;


    @Override
    public void executeCommand(Command command) {
        if (command == null) throw new DomainValidationException("Drone", "Command cannot be null");
        var droneUuid = command.getExplorationDroneId();
        var droneId = ExplorationDroneId.of(droneUuid);
        if (command.isSpawn()) {
            explorationDroneRepository.findById(droneId).orElseThrow(() -> new DomainValidationException("Drone", "Drone already exists"));
            spawnService.spawn(droneId);
        } else {
            var explorationDrone = explorationDroneRepository.findById(droneId).orElseThrow(() -> new DomainValidationException("Drone", "Drone does not exist"));
            if (command.isMove()) {
                movementService.move(explorationDrone, command.getMoveDirection());
            } else if (command.isExplore())
                movementService.explore(explorationDrone);
            else if (command.isGohome())
                movementService.goHome(explorationDrone);
            else if (command.isTransport()) {
                var entryPlanet = explorationDrone.getPlanet();
                var exitPlanet = hyperTunnelInterface.findByEntryPlanet(entryPlanet);
                movementService.transport(explorationDrone, exitPlanet);
            } else if (command.isMine())
                miningService.mine(explorationDrone);
        }
    }

    @Override
    public Load getExplorationDroneLoad(UUID explorationDroneId) {
        return spawnService.getExplorationDroneLoad(explorationDroneId);
    }

    @Override
    public UUID getExplorationDronePlanet(UUID explorationDroneId) {
        return spawnService.getDronePlanet(explorationDroneId);
    }

    @Override
    public String getPlanetType(UUID planetId) {
        return planetService.getPlanetType(planetId).getValue();
    }

    @Override
    public Uranium getPlanetUraniumAmount(UUID planetId) {
        return planetService.getUranium(planetId);
    }

    @Override
    public List<UUID> getPlanetExplorationDrones(UUID planetId) {
        return planetService.getDronesOf(planetId);
    }

    @Override
    public List<UUID> getPlanets() {
        return planetService.getPlanets();
    }

    @Override
    public UUID installHyperspaceEnergyTunnel(UUID entryPlanetId, UUID exitPlanetId) {

        return hyperspaceEnergyTunnelUseCases.installHyperspaceEnergyTunnel(entryPlanetId, exitPlanetId);
    }

    @Override
    public void relocateHyperspaceEnergyTunnel(UUID hyperspaceEnergyTunnelId, UUID entryPlanetId, UUID exitPlanetId) {
        hyperspaceEnergyTunnelUseCases.relocateHyperspaceEnergyTunnel(hyperspaceEnergyTunnelId, entryPlanetId, exitPlanetId);

    }

    @Override
    public void shutdownHyperspaceEnergyTunnel(UUID hyperspaceEnergyTunnelId) {
        hyperspaceEnergyTunnelUseCases.shutdownHyperspaceEnergyTunnel(hyperspaceEnergyTunnelId);

    }

    @Override
    public UUID resetAll() {
        spawnService.deleteAll();
        return planetService.resetAll();
    }
}
