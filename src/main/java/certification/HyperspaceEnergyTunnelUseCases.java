package certification;

import java.util.UUID;

public interface HyperspaceEnergyTunnelUseCases {
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
    public void relocateHyperspaceEnergyTunnel( UUID hyperspaceEnergyTunnelId, UUID entryPlanetId, UUID exitPlanetId );

    /**
     * Shut down an existing hyperspace energy tunnel
     * @param hyperspaceEnergyTunnelId - id of the hyperspace energy tunnel to be shut down
     * @throws ExplorationDroneControlException in case of invalid parameters
     */
    public void shutdownHyperspaceEnergyTunnel( UUID hyperspaceEnergyTunnelId );
}
