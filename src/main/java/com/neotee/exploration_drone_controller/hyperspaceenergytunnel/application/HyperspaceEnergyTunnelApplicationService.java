package com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnel;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.domain.HyperspaceEnergyTunnelRepository;
import com.neotee.exploration_drone_controller.planet.application.PlanetService;
import com.neotee.exploration_drone_controller.planet.application.PlanetValidator;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HyperspaceEnergyTunnelApplicationService {
    private final HyperspaceEnergyTunnelRepository hyperspaceEnergyTunnelRepository;
    private final PlanetService planetService;
    private final HyperspaceEnergyTunnelMapper hyperspaceEnergyTunnelMapper;
    private final TunnelValidator tunnelValidator;
    private final PlanetValidator planetValidator;

    public HyperspaceEnergyTunnelDTO install(HyperspaceEnergyTunnelDTO hyperspaceEnergyTunnelDTO) {
        if (hyperspaceEnergyTunnelDTO == null)
            throw new ExplorationDroneControlException("hyperspaceEnergyTunnelDTO is null");
        if (hyperspaceEnergyTunnelDTO.getId() == null) throw new ExplorationDroneControlException("id is null");
        if (hyperspaceEnergyTunnelDTO.getExitPlanetId() == null)
            throw new ExplorationDroneControlException("exitPlanetId is null");
        if (hyperspaceEnergyTunnelDTO.getEntryPlanetId() == null)
            throw new ExplorationDroneControlException("entryPlanetId is null");

        UUID entryPlanetId = hyperspaceEnergyTunnelDTO.getEntryPlanetId();
        UUID exitPlanetId = hyperspaceEnergyTunnelDTO.getExitPlanetId();

        Planet entryPlanet = planetService.findPlanetById(entryPlanetId);
        Planet exitPlanet = planetService.findPlanetById(exitPlanetId);
        if (entryPlanet == null || exitPlanet == null) throw new ExplorationDroneControlException("Planet not found");

        HyperspaceEnergyTunnel tunnel = new HyperspaceEnergyTunnel();
        tunnel.install(entryPlanet, exitPlanet);

        planetService.save(entryPlanet);
        planetService.save(exitPlanet);
        hyperspaceEnergyTunnelRepository.save(tunnel);
        return hyperspaceEnergyTunnelMapper.toDTO(tunnel);
    }

    public List<HyperspaceEnergyTunnelDTO> findAll() {
        List<HyperspaceEnergyTunnel> tunnels = hyperspaceEnergyTunnelRepository.findAll();
        List<HyperspaceEnergyTunnelDTO> dtos = new ArrayList<>();
        for (HyperspaceEnergyTunnel tunnel : tunnels) {
            dtos.add(hyperspaceEnergyTunnelMapper.toDTO(tunnel));
        }
        return dtos;
    }

    public HyperspaceEnergyTunnelDTO findById(UUID tunnelId) {
        HyperspaceEnergyTunnel tunnel = hyperspaceEnergyTunnelRepository.findById(tunnelId).orElse(null);
        if (tunnel == null) return null;
        return hyperspaceEnergyTunnelMapper.toDTO(tunnel);
    }

    public void shutdown(UUID tunnelId) {
        if (tunnelId == null)
            throw new ExplorationDroneControlException("tunnel not found");
        HyperspaceEnergyTunnel tunnel = (HyperspaceEnergyTunnel) tunnelValidator.validateTunnelExists(tunnelId);
        tunnel.shutdown();
        hyperspaceEnergyTunnelRepository.save(tunnel);
    }


    public HyperspaceEnergyTunnelDTO relocate( HyperspaceEnergyTunnelDTO hyperspaceEnergyTunnelDTO) {
        if (hyperspaceEnergyTunnelDTO == null)
            throw new ExplorationDroneControlException("hyperspaceEnergyTunnelDTO is null");
        if (hyperspaceEnergyTunnelDTO.getId() == null) throw new ExplorationDroneControlException("id is null");
        if (hyperspaceEnergyTunnelDTO.getExitPlanetId() == null)
            throw new ExplorationDroneControlException("exitPlanetId is null");
        if (hyperspaceEnergyTunnelDTO.getEntryPlanetId() == null)
            throw new ExplorationDroneControlException("entryPlanetId is null");
        UUID entryPlanetId = hyperspaceEnergyTunnelDTO.getEntryPlanetId();
        UUID exitPlanetId = hyperspaceEnergyTunnelDTO.getExitPlanetId();
        UUID tunnelId = hyperspaceEnergyTunnelDTO.getId();

        Planet entryPlanet = planetValidator.validatePlanetExists(entryPlanetId);
        Planet exitPlanet = planetValidator.validatePlanetExists(exitPlanetId);

        HyperspaceEnergyTunnel tunnel = hyperspaceEnergyTunnelRepository.findById(tunnelId)
                .orElseThrow(() -> new ExplorationDroneControlException("Tunnel not found"));
        tunnel.relocate(entryPlanet, exitPlanet);
        hyperspaceEnergyTunnelRepository.save(tunnel);
        return hyperspaceEnergyTunnelMapper.toDTO(tunnel);
    }
}
