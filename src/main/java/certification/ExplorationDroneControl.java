package certification;


import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;

import java.util.List;
import java.util.UUID;

public interface ExplorationDroneControl {
    /**
     * Execute the given command
     * @param command - command to be executed
     * @throws ExplorationDroneControlException if the command is invalid, or cannot be executed
     */
    public void executeCommand( Command command );

    /**
     * @param explorationDroneId
     * @throws ExplorationDroneControlException if explorationDroneId is not a valid exploration drone id
     * @return current load of the exploration drone
     */
    Load getExplorationDroneLoad(UUID explorationDroneId );

    /**
     * @param explorationDroneId
     * @throws ExplorationDroneControlException if explorationDroneId is not a valid exploration drone id
     * @return id of the planet the exploration drone is currently located on
     */
    UUID getExplorationDronePlanet( UUID explorationDroneId );

    /**
     * @param planetId - Id of the planet
     * @throws ExplorationDroneControlException if planetId is not a valid planet id
     * @return Type of the planet, either "space station", "regular", or "unknown"
     *      "regular" means "no space station, but has been visited"
     *      "unknown" means "we know there is a planet, but we haven't visited it yet"
     */
    String getPlanetType( UUID planetId );

    /**
     * @param planetId
     * @throws ExplorationDroneControlException if planetId is not a valid planet id
     * @return the amount of uranium still left on the planet (can be an empty amount, but not null)
     */
    Uranium getPlanetUraniumAmount(UUID planetId );

    /**
     * @param planetId
     * @throws ExplorationDroneControlException if planetId is not a valid planet id
     * @return List of exploration drone ids currently located on this planet (List can be empty)
     */
    List<UUID> getPlanetExplorationDrones( UUID planetId );


    /**
     * @return List of all planet ids currently known
     */
    List<UUID> getPlanets();


    /**
     * Install a new hyperspace energy tunnel.
     * @param entryPlanetId - The entry planet of the hyperspace energy tunnel, as UUID.
     * @param exitPlanetId - The exit planet of the hyperspace energy tunnel, as UUID.
     * @throws ExplorationDroneControlException in case of invalid parameters
     * @return The UUID of the new installed hyperspace energy tunnel
     */
    public UUID installHyperspaceEnergyTunnel(UUID entryPlanetId, UUID exitPlanetId );

    /**
     * Relocate an existing hyperspace energy tunnel to new planet(s).
     * @param hyperspaceEnergyTunnelId - id of the hyperspace energy tunnel to be relocated
     * @param entryPlanetId - new entry planet of the hyperspace energy tunnel, as UUID.
     * @param exitPlanetId - new exit planet of the hyperspace energy tunnel, as UUID.
     * @throws ExplorationDroneControlException in case of invalid parameters
     */
    public void relocateHyperspaceEnergyTunnel(UUID hyperspaceEnergyTunnelId, UUID entryPlanetId, UUID exitPlanetId );

    /**
     * Shut down an existing hyperspace energy tunnel
     * @param hyperspaceEnergyTunnelId - id of the hyperspace energy tunnel to be shut down
     * @throws ExplorationDroneControlException in case of invalid parameters
     */
    public void shutdownHyperspaceEnergyTunnel(UUID hyperspaceEnergyTunnelId );


    /**
     * Delete all explorationDrones, planets, and hyperspaceEnergyTunnels. Only the space station remains, with 0 uranium.
     * @return UUID of the space station
     */
    public UUID resetAll();
}
