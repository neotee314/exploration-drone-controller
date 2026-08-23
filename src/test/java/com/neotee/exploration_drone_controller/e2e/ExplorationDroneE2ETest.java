package com.neotee.exploration_drone_controller.e2e;

import com.neotee.exploration_drone_controller.domainprimitives.PlanetType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandRequestDto;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto.HyperspaceEnergyTunnelRequestDto;
import com.neotee.exploration_drone_controller.planet.application.dto.PlanetResponseDto;
import com.neotee.exploration_drone_controller.planet.application.dto.UraniumRequestDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExplorationDroneE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private List<PlanetResponseDto> planets;
    private Map<UUID, PlanetResponseDto> planetMap;
    private PlanetResponseDto origin;
    private Set<UUID> planetIds;
    private UUID drone1;
    private UUID drone2;
    private UUID drone1MiningPlanet;

    @BeforeEach
    void setUp() throws Exception {
        resetMap();
        generateMap(20);
        planets = getAllPlanets();
        planetMap = planets.stream()
                .collect(Collectors.toMap(PlanetResponseDto::planetId, p -> p));
        planetIds = planets.stream()
                .map(PlanetResponseDto::planetId)
                .collect(Collectors.toSet());

        assertEquals(20, planets.size(), "Map should contain exactly 20 planets");

        origin = planets.stream()
                .filter(this::isOrigin)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No origin planet found"));

        // Give every non-origin planet uranium
        for (PlanetResponseDto planet : planets) {
            if (!isOrigin(planet)) {
                addUranium(planet.planetId(), 20);
            }
        }
    }

    @Test
    void completeExplorationDroneEndToEndScenario() throws Exception {
        // ================================================================
        // 🗺️  THE MAP - 20 Planets Connected Like a Grid
        // ================================================================
        //
        //                    [ORIGIN - Space Station] 🚀
        //                         ⬆️  ⬅️  ➡️  ⬇️
        //                         │   │   │   │
        //              [P1]────[P2]───[P3]───[P4]────[P5]
        //               │     │    │    │    │     │
        //              [P6]───[P7]──[P8]──[P9]───[P10]
        //               │     │    │    │    │     │
        //              [P11]──[P12]─[P13]─[P14]──[P15]
        //               │     │    │    │    │     │
        //              [P16]──[P17]─[P18]─[P19]──[P20]
        //
        //    🛸 = Drone 1          🛸 = Drone 2
        //    🌉 = Hyperspace Tunnel
        //    ⛏️ = Uranium Mining Site
        //    🚀 = Origin Planet (Space Station)
        //    🪐 = Regular Planet
        // ================================================================

        // ================================================================
        // 📋 PHASE 1: MAP VERIFICATION
        // ================================================================
        //   🗺️  Verify we have exactly 20 unique planets
        // ================================================================
        assertEquals(20, planets.size());
        assertEquals(20, planetIds.size());

        // ================================================================
        // 📋 PHASE 2: SPAWN DRONES
        // ================================================================
        //   🚀                          🚀
        //   [🛸]  [🛸]  ← Two drones spawned at Origin
        //   🚀                          🚀
        // ================================================================
        drone1 = spawnDrone();
        drone2 = spawnDrone();

        assertNotNull(drone1);
        assertNotNull(drone2);
        assertNotEquals(drone1, drone2);
        assertEquals(origin.planetId(), getDronePlanet(drone1));
        assertEquals(origin.planetId(), getDronePlanet(drone2));

        // ================================================================
        // 📋 PHASE 3: DRONE 1 - EXPLORE
        // ================================================================
        //   🚀
        //   [🛸] ──explore──► 🪐(P2)  ← Drone 1 moves randomly
        //   🚀
        // ================================================================
        sendCommand(drone1, "explore");
        drone1MiningPlanet = getDronePlanet(drone1);
        assertTrue(planetIds.contains(drone1MiningPlanet));
        assertNotEquals(origin.planetId(), drone1MiningPlanet);

        // ================================================================
        // 📋 PHASE 4: DRONE 1 - MINE URANIUM
        // ================================================================
        //   🚀
        //   [🛸] ──mine──► ⛏️  +20 Uranium  ← Drone 1 mines
        //   🚀
        // ================================================================
        sendCommand(drone1, "mine");

        // ================================================================
        // 📋 PHASE 5: DRONE 1 - RETURN HOME
        // ================================================================
        //   🚀
        //   [🛸] ──gohome──► 🚀  ← Drone 1 returns with uranium
        //   🚀
        // ================================================================
        goHomeSafely(drone1, origin.planetId());
        assertEquals(origin.planetId(), getDronePlanet(drone1));

        // ================================================================
        // 📋 PHASE 6: DRONE 2 - MOVE TO DIFFERENT PLANET
        // ================================================================
        //   🚀
        //   [🛸] ──south──► 🪐(P7)  ← Drone 2 moves to a DIFFERENT planet
        //   🚀                        (not the one Drone 1 mined)
        // ================================================================
        UUID targetForDrone2 = findReachablePlanet(origin.planetId(), drone1MiningPlanet);
        List<Step> pathForDrone2 = findPath(origin.planetId(), targetForDrone2, planetMap);
        assertFalse(pathForDrone2.isEmpty());

        for (Step step : pathForDrone2) {
            sendCommand(drone2, step.direction());
            assertEquals(step.to(), getDronePlanet(drone2));
        }

        // ================================================================
        // 📋 PHASE 7: DRONE 2 - MINE URANIUM
        // ================================================================
        //   🚀
        //   [🛸] ──mine──► ⛏️  +20 Uranium  ← Drone 2 mines (different planet)
        //   🚀
        // ================================================================
        UUID drone2MiningPlanet = getDronePlanet(drone2);
        assertNotEquals(drone1MiningPlanet, drone2MiningPlanet);
        sendCommand(drone2, "mine");

        // ================================================================
        // 📋 PHASE 8: DRONE 2 - RETURN HOME
        // ================================================================
        //   🚀
        //   [🛸] ──gohome──► 🚀  ← Drone 2 returns with uranium
        //   🚀
        // ================================================================
        goHomeSafely(drone2, origin.planetId());
        assertEquals(origin.planetId(), getDronePlanet(drone2));

        // ================================================================
        // 📋 PHASE 9: INSTALL 4 HYPERSPACE TUNNELS
        // ================================================================
        //   🌉 Tunnel 1: 🪐P1 ←───────→ 🪐P2
        //   🌉 Tunnel 2: 🪐P3 ←───────→ 🪐P4
        //   🌉 Tunnel 3: 🪐P5 ←───────→ 🪐P6
        //   🌉 Tunnel 4: 🪐P7 ←───────→ 🪐P8
        //
        //   These tunnels create shortcuts between distant planets!
        // ================================================================
        List<UUID> tunnelPlanets = planets.stream()
                .map(PlanetResponseDto::planetId)
                .filter(id -> !id.equals(origin.planetId()))
                .limit(8)
                .collect(Collectors.toList());

        assertEquals(8, tunnelPlanets.size());

        UUID tunnel1 = installTunnel(tunnelPlanets.get(0), tunnelPlanets.get(1));
        UUID tunnel2 = installTunnel(tunnelPlanets.get(2), tunnelPlanets.get(3));
        UUID tunnel3 = installTunnel(tunnelPlanets.get(4), tunnelPlanets.get(5));
        UUID tunnel4 = installTunnel(tunnelPlanets.get(6), tunnelPlanets.get(7));

        assertNotNull(tunnel1);
        assertNotNull(tunnel2);
        assertNotNull(tunnel3);
        assertNotNull(tunnel4);

        // ================================================================
        // 📋 PHASE 10: VERIFY ALL TUNNELS EXIST
        // ================================================================
        //   🌉 Should see 4 active tunnels in the system
        // ================================================================
        List<?> tunnels = getAllTunnels();
        assertEquals(4, tunnels.size());

        // ================================================================
        // 📋 PHASE 11: DRONE 1 - USE TUNNEL 1
        // ================================================================
        //   🚀 → 🪐P1 → 🌉 → 🪐P2
        //   [🛸] moves to tunnel entry, then transports instantly!
        // ================================================================
        UUID tunnel1Entry = tunnelPlanets.get(0);
        UUID tunnel1Exit = tunnelPlanets.get(1);

        moveDroneTo(drone1, origin.planetId(), tunnel1Entry, planetMap);
        assertEquals(tunnel1Entry, getDronePlanet(drone1));

        sendCommand(drone1, "transport");
        assertEquals(tunnel1Exit, getDronePlanet(drone1));

        // ================================================================
        // 📋 PHASE 12: DRONE 2 - USE TUNNEL 2
        // ================================================================
        //   🚀 → 🪐P3 → 🌉 → 🪐P4
        //   [🛸] moves to tunnel entry, then transports instantly!
        // ================================================================
        UUID tunnel2Entry = tunnelPlanets.get(2);
        UUID tunnel2Exit = tunnelPlanets.get(3);

        moveDroneTo(drone2, origin.planetId(), tunnel2Entry, planetMap);
        assertEquals(tunnel2Entry, getDronePlanet(drone2));

        sendCommand(drone2, "transport");
        assertEquals(tunnel2Exit, getDronePlanet(drone2));

        // ================================================================
        // 📋 PHASE 13: DRONES RETURN HOME (MANUAL PATH)
        // ================================================================
        //   After transport, drones are in "transported" state.
        //   They cannot use "gohome" - they must walk back manually!
        //
        //   🪐P2 → step1 → step2 → ... → 🚀
        //   [🛸] moves step by step back to origin
        // ================================================================

        // Drone 1 - manually move back to origin
        List<Step> pathHome1 = findPath(tunnel1Exit, origin.planetId(), planetMap);
        assertFalse(pathHome1.isEmpty(), "No path home for drone 1");
        for (Step step : pathHome1) {
            sendCommand(drone1, step.direction());
            assertEquals(step.to(), getDronePlanet(drone1));
        }
        assertEquals(origin.planetId(), getDronePlanet(drone1));

        // Drone 2 - manually move back to origin
        List<Step> pathHome2 = findPath(tunnel2Exit, origin.planetId(), planetMap);
        assertFalse(pathHome2.isEmpty(), "No path home for drone 2");
        for (Step step : pathHome2) {
            sendCommand(drone2, step.direction());
            assertEquals(step.to(), getDronePlanet(drone2));
        }
        assertEquals(origin.planetId(), getDronePlanet(drone2));

        // ================================================================
        // 📋 PHASE 14: CHECK COMMAND HISTORIES
        // ================================================================
        //   📜 Verify both drones have command history
        // ================================================================
        List<?> drone1History = getCommandHistory(drone1);
        List<?> drone2History = getCommandHistory(drone2);

        assertFalse(drone1History.isEmpty());
        assertFalse(drone2History.isEmpty());

        // ================================================================
        // 📋 PHASE 15: GET INDIVIDUAL DRONES
        // ================================================================
        //   🔍 Fetch each drone by ID from the system
        // ================================================================
        getDrone(drone1);
        getDrone(drone2);

        // ================================================================
        // 📋 PHASE 16: GET ALL DRONES
        // ================================================================
        //   📋 List all drones in the system (at least 2)
        // ================================================================
        List<?> allDrones = getAllDrones();
        assertTrue(allDrones.size() >= 2);

        // ================================================================
        // 📋 PHASE 17: CLEAR DRONE 2 COMMAND HISTORY
        // ================================================================
        //   🗑️ Clear drone 2's command history
        //   📜 Should be empty after clearing
        // ================================================================
        mockMvc.perform(delete("/api/v1/explorationDrones/{droneId}/commands", drone2))
                .andExpect(status().isNoContent());

        assertTrue(getCommandHistory(drone2).isEmpty());

        // ================================================================
        // 📋 PHASE 18: SHUTDOWN ALL TUNNELS
        // ================================================================
        //   🚫 Shutdown all 4 tunnels
        //   🌉 Each tunnel becomes INACTIVE (not deleted)
        //   ✅ Verify all 4 tunnels are now INACTIVE
        // ================================================================
        shutdownTunnel(tunnel1);
        shutdownTunnel(tunnel2);
        shutdownTunnel(tunnel3);
        shutdownTunnel(tunnel4);

        String remainingTunnelsResponse = mockMvc.perform(
                        get("/api/v1/hyperspaceenergytunnels")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<?> remainingTunnels = objectMapper.readValue(
                remainingTunnelsResponse,
                new TypeReference<List<Object>>() {}
        );

        // Verify all tunnels are now INACTIVE
        assertEquals(4, remainingTunnels.size(), "All 4 tunnels should still exist");
        for (Object tunnel : remainingTunnels) {
            Map<String, Object> tunnelMap = (Map<String, Object>) tunnel;
            assertEquals("INACTIVE", tunnelMap.get("tunnelState"),
                    "Tunnel should be INACTIVE after shutdown");
        }

        // ================================================================
        // 🎉 TEST COMPLETE - All phases passed successfully!
        // ================================================================
        //   ✅ Map generated correctly
        //   ✅ Two drones spawned and moved
        //   ✅ Both drones mined uranium
        //   ✅ Both drones returned home safely
        //   ✅ 4 tunnels installed and verified
        //   ✅ Both drones used tunnels successfully
        //   ✅ Drones returned home manually after transport
        //   ✅ Command histories work correctly
        //   ✅ Drone listing works
        //   ✅ Command history cleared successfully
        //   ✅ All tunnels shutdown successfully
        // ================================================================
    }

    // ================================================================
    // 🔧 HELPER METHODS
    // ================================================================

    private void goHomeSafely(UUID droneId, UUID originPlanetId) throws Exception {
        int safetyCounter = 20;

        if (getDronePlanet(droneId).equals(originPlanetId)) {
            return;
        }

        sendCommand(droneId, "gohome");

        while (!getDronePlanet(droneId).equals(originPlanetId) && safetyCounter-- > 0) {
            sendCommand(droneId, "gohome");
        }

        assertEquals(originPlanetId, getDronePlanet(droneId));
    }

    private UUID findReachablePlanet(UUID start, UUID exclude) {
        return planets.stream()
                .map(PlanetResponseDto::planetId)
                .filter(id -> !id.equals(start))
                .filter(id -> exclude == null || !id.equals(exclude))
                .filter(id -> !findPath(start, id, planetMap).isEmpty())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No reachable planet found"));
    }

    // ================================================================
    // 🗺️ MAP OPERATIONS
    // ================================================================

    private void resetMap() throws Exception {
        mockMvc.perform(post("/api/v1/planets/reset")
                .contentType(MediaType.APPLICATION_JSON));
    }

    private void generateMap(int count) throws Exception {
        mockMvc.perform(post("/api/v1/planets/generate/{planetCount}", count)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private List<PlanetResponseDto> getAllPlanets() throws Exception {
        String response = mockMvc.perform(get("/api/v1/planets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, new TypeReference<List<PlanetResponseDto>>() {});
    }

    private boolean isOrigin(PlanetResponseDto planet) {
        return PlanetType.SPACE_STATION.getValue().equals(planet.planetType());
    }

    // ================================================================
    // ⛏️ URANIUM OPERATIONS
    // ================================================================

    private void addUranium(UUID planetId, int amount) throws Exception {
        UraniumRequestDto dto = new UraniumRequestDto();
        dto.setAmount(amount);

        mockMvc.perform(post("/api/v1/planets/{planetId}/uraniums", planetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    // ================================================================
    // 🛸 DRONE OPERATIONS
    // ================================================================

    private UUID spawnDrone() throws Exception {
        String response = mockMvc.perform(post("/api/v1/explorationDrones/spawn")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(response).get("id").asText());
    }

    private String sendCommand(UUID droneId, String command) throws Exception {
        CommandRequestDto dto = new CommandRequestDto();
        dto.setCommandString(command);

        System.out.println("COMMAND = >" + command + "<");

        return mockMvc.perform(post("/api/v1/explorationDrones/{droneId}/commands", droneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private UUID getDronePlanet(UUID droneId) throws Exception {
        String response = mockMvc.perform(get("/api/v1/explorationDrones/{droneId}", droneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(response).get("planetId").asText());
    }

    private void getDrone(UUID droneId) throws Exception {
        mockMvc.perform(get("/api/v1/explorationDrones/{droneId}", droneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private List<?> getCommandHistory(UUID droneId) throws Exception {
        String response = mockMvc.perform(get("/api/v1/explorationDrones/{droneId}/commands", droneId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, new TypeReference<List<Object>>() {});
    }

    private List<?> getAllDrones() throws Exception {
        String response = mockMvc.perform(get("/api/v1/explorationDrones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, new TypeReference<List<Object>>() {});
    }

    // ================================================================
    // 🌉 TUNNEL OPERATIONS
    // ================================================================

    private UUID installTunnel(UUID entryPlanetId, UUID exitPlanetId) throws Exception {
        HyperspaceEnergyTunnelRequestDto dto = new HyperspaceEnergyTunnelRequestDto();
        dto.setEntryPlanetId(entryPlanetId);
        dto.setExitPlanetId(exitPlanetId);

        String response = mockMvc.perform(post("/api/v1/hyperspaceenergytunnels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(response).get("id").asText());
    }

    private void shutdownTunnel(UUID tunnelId) throws Exception {
        mockMvc.perform(delete("/api/v1/hyperspaceenergytunnels/{tunnelId}/shutdown", tunnelId))
                .andExpect(status().isNoContent());
    }

    private List<?> getAllTunnels() throws Exception {
        String response = mockMvc.perform(get("/api/v1/hyperspaceenergytunnels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, new TypeReference<List<Object>>() {});
    }

    // ================================================================
    // 🧭 GRAPH / MOVEMENT
    // ================================================================

    private void moveDroneTo(UUID droneId, UUID start, UUID target, Map<UUID, PlanetResponseDto> planets) throws Exception {
        List<Step> path = findPath(start, target, planets);
        assertFalse(path.isEmpty(), "No path found from " + start + " to " + target);

        for (Step step : path) {
            sendCommand(droneId, step.direction());
            assertEquals(step.to(), getDronePlanet(droneId));
        }
    }

    private List<Step> findPath(UUID start, UUID target, Map<UUID, PlanetResponseDto> planets) {
        record Node(UUID planet, List<Step> path) {}

        Queue<Node> queue = new ArrayDeque<>();
        Set<UUID> visited = new HashSet<>();

        queue.add(new Node(start, List.of()));
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.planet().equals(target)) {
                return current.path();
            }

            PlanetResponseDto planet = planets.get(current.planet());
            if (planet == null) {
                continue;
            }

            List<Step> neighbours = List.of(
                    new Step("north", current.planet(), planet.northId()),
                    new Step("east", current.planet(), planet.eastId()),
                    new Step("south", current.planet(), planet.southId()),
                    new Step("west", current.planet(), planet.westId())
            );

            for (Step step : neighbours) {
                if (step.to() == null || !visited.add(step.to())) {
                    continue;
                }

                List<Step> newPath = new ArrayList<>(current.path());
                newPath.add(step);
                queue.add(new Node(step.to(), newPath));
            }
        }

        return List.of();
    }

    private record Step(String direction, UUID from, UUID to) {}
}