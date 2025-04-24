package anticorruption;

import certification.ExplorationDroneControl;
import certification.ExplorationDroneControlException;
import certification.HyperspaceEnergyTunnelUseCases;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.explorationdrone.application.DroneValidator;
import com.neotee.exploration_drone_controller.explorationdrone.application.MiningService;
import com.neotee.exploration_drone_controller.explorationdrone.application.MovementService;
import com.neotee.exploration_drone_controller.explorationdrone.application.ExplorationDroneService;
import com.neotee.exploration_drone_controller.planet.application.PlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ComponentScan("com.neotee.exploration_drone_controller")
public class MyFantasticExplorationDroneControl implements ExplorationDroneControl {

    private final ExplorationDroneService explorationDroneService;
    private final PlanetService planetService;
    private final HyperspaceEnergyTunnelUseCases hyperspaceEnergyTunnelUseCases;
    private final MovementService movementService;
    private final MiningService miningService;



    @Override
    public void executeCommand(Command command) {
        if (command == null)
            throw new ExplorationDroneControlException("Command cannot be null");
        UUID droneId = command.getExplorationDroneId();


        if (command.isSpawn()) planetService.spawn(droneId);
        else if (command.isMove())
            movementService.move(droneId, command.getMoveDirection());
        else if (command.isExplore())
            movementService.explore(droneId);
        else if (command.isGohome())
            movementService.goHome(droneId);
        else if (command.isTransport())
            movementService.transport(droneId);
        else if (command.isMine())
            miningService.mine(droneId);

        else throw new ExplorationDroneControlException("Unknown command type");

    }

    @Override
    public Load getExplorationDroneLoad(UUID explorationDroneId) {
        return explorationDroneService.getExplorationDroneLoad(explorationDroneId);
    }

    @Override
    public UUID getExplorationDronePlanet(UUID explorationDroneId) {
        return planetService.findExplorationDronePlanet(explorationDroneId);
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
        explorationDroneService.deleteAll();
        return planetService.resetAll();
    }
}
