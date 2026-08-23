package com.neotee.exploration_drone_controller.anticorruption;

import com.neotee.exploration_drone_controller.certification.ExplorationDroneControl;
import com.neotee.exploration_drone_controller.domainprimitives.*;
import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import com.neotee.exploration_drone_controller.explorationdrone.application.service.ExplorationDroneApplicationService;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.service.HyperspaceEnergyTunnelApplicationService;
import com.neotee.exploration_drone_controller.planet.application.service.PlanetApplicationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MyFantasticExplorationDroneControl implements ExplorationDroneControl {
    private final ExplorationDroneApplicationService explorationDroneApplicationService;
    private final PlanetApplicationService planetService;
    private final HyperspaceEnergyTunnelApplicationService hyperspaceEnergyTunnelApplicationService;

    @Override
    public void executeCommand(Command command) {
        if (command==null) throw new DomainValidationException("MyFantasticExplorationDroneControl","command cannot be null");
        explorationDroneApplicationService.sendCommand(command);

    }

    @Override
    public Load getExplorationDroneLoad(UUID explorationDroneId) {
        var id = ExplorationDroneId.of(explorationDroneId);
        return explorationDroneApplicationService.getExplorationDroneLoad(id);
    }

    @Override
    public UUID getExplorationDronePlanet(UUID explorationDroneId) {
        var id = ExplorationDroneId.of(explorationDroneId);
        return explorationDroneApplicationService.getPlanetWhichDroneIsOn(id).getId().getId();
    }

    @Override
    public String getPlanetType(UUID planetId) {
        return planetService.getPlanetType(PlanetId.of(planetId)).toString();
    }

    @Override
    public Uranium getPlanetUraniumAmount(UUID planetId) {
        return planetService.getUranium(PlanetId.of(planetId));
    }

    @Override
    public List<UUID> getPlanetExplorationDrones(UUID planetId) {
        var droneIds = explorationDroneApplicationService.getAllDronesOnPlanet(PlanetId.of(planetId));
        return droneIds.stream().map(drone -> drone.getId().getId()).toList();
    }

    @Override
    public List<UUID> getPlanets() {
        return planetService.getAllPlanets().stream().map(p -> p.getId().getId()).toList();
    }

    @Override
    public UUID installHyperspaceEnergyTunnel(UUID entryPlanetId, UUID exitPlanetId) {
        var entryPlanetID = PlanetId.of(entryPlanetId);
        var exitPlanetID = PlanetId.of(exitPlanetId);
        return hyperspaceEnergyTunnelApplicationService.install(entryPlanetID, exitPlanetID).getId().getId();
    }

    @Override
    public void relocateHyperspaceEnergyTunnel(UUID id, UUID entryPlanetId, UUID exitPlanetId) {
        var entryPlanetID = PlanetId.of(entryPlanetId);
        var exitPlanetID = PlanetId.of(exitPlanetId);
        hyperspaceEnergyTunnelApplicationService.relocate(HyperspaceEnergyTunnelId.of(id), entryPlanetID, exitPlanetID);

    }

    @Override
    public void shutdownHyperspaceEnergyTunnel(UUID hyperspaceEnergyTunnelId) {
        hyperspaceEnergyTunnelApplicationService.shutdown(HyperspaceEnergyTunnelId.of(hyperspaceEnergyTunnelId));

    }

    @Override
    public UUID resetAll() {
        return planetService.resetAll().getId().getId();
    }
}
