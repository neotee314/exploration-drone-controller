package anticorruption;

import certification.HyperspaceEnergyTunnelUseCases;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.HyperspaceEnergyTunnelService;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnelRepository;
import com.neotee.exploration_drone_controller.planet.application.PlanetService;
import com.neotee.exploration_drone_controller.planet.application.PlanetValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MyFantasticHyperspaceEnergyTunnelUseCasesService implements HyperspaceEnergyTunnelUseCases {

    private final HyperspaceEnergyTunnelRepository hyperspaceEnergyTunnelRepository;
    private final HyperspaceEnergyTunnelService hyperspaceEnergyTunnelService;
    private final PlanetService planetService;
    private final PlanetValidator planetValidator;

    @Override
    public UUID installHyperspaceEnergyTunnel(UUID entryPlanetId, UUID exitPlanetId) {

        return hyperspaceEnergyTunnelService.installHyperspaceEnergyTunnel(entryPlanetId, exitPlanetId);
    }

    @Override
    public void relocateHyperspaceEnergyTunnel(UUID tunnelId, UUID entryPlanetId, UUID exitPlanetId) {

        hyperspaceEnergyTunnelService.relocateHyperspaceEnergyTunnel(tunnelId, entryPlanetId, exitPlanetId);

    }

    @Override
    public void shutdownHyperspaceEnergyTunnel(UUID tunnelId) {


        hyperspaceEnergyTunnelService.shutdownHyperspaceEnergyTunnel(tunnelId);

    }
}
