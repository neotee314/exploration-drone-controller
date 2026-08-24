package com.neotee.exploration_drone_controller.e2e;

import com.neotee.exploration_drone_controller.config.TestContainersConfiguration;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetType;
import com.neotee.exploration_drone_controller.explorationdrone.application.dto.CommandRequestDto;
import com.neotee.exploration_drone_controller.hyperspaceenergytunnel.application.dto.HyperspaceEnergyTunnelRequestDto;
import com.neotee.exploration_drone_controller.planet.application.dto.PlanetResponseDto;
import com.neotee.exploration_drone_controller.planet.application.dto.UraniumRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
public class ExplorationDroneE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();
    private String baseUrl;

    private List<PlanetResponseDto> planets;
    private Map<UUID, PlanetResponseDto> planetMap;
    private PlanetResponseDto origin;
    private Set<UUID> planetIds;
    private UUID drone1;
    private UUID drone2;
    private UUID drone1MiningPlanet;

    @BeforeEach
    void setUp() throws Exception {
        baseUrl = "http://localhost:" + port + "/api/v1";

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

        for (PlanetResponseDto planet : planets) {
            if (!isOrigin(planet)) {
                addUranium(planet.planetId(), 20);
            }
        }
    }

    @Test
    void completeExplorationDroneEndToEndScenario() throws Exception {
        // ================================================================
        // 📋 PHASE 1: MAP VERIFICATION
        // ================================================================
        assertEquals(20, planets.size());
        assertEquals(20, planetIds.size());

        // ================================================================
        // 📋 PHASE 2: SPAWN DRONES
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
        sendCommand(drone1, "explore");
        drone1MiningPlanet = getDronePlanet(drone1);
        assertTrue(planetIds.contains(drone1MiningPlanet));
        assertNotEquals(origin.planetId(), drone1MiningPlanet);

        // ================================================================
        // 📋 PHASE 4: DRONE 1 - MINE URANIUM
        // ================================================================
        sendCommand(drone1, "mine");

        // ================================================================
        // 📋 PHASE 5: DRONE 1 - RETURN HOME
        // ================================================================
        goHomeSafely(drone1, origin.planetId());
        assertEquals(origin.planetId(), getDronePlanet(drone1));

        // ================================================================
        // 📋 PHASE 6: DRONE 2 - MOVE TO DIFFERENT PLANET
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
        UUID drone2MiningPlanet = getDronePlanet(drone2);
        assertNotEquals(drone1MiningPlanet, drone2MiningPlanet);
        sendCommand(drone2, "mine");

        // ================================================================
        // 📋 PHASE 8: DRONE 2 - RETURN HOME
        // ================================================================
        goHomeSafely(drone2, origin.planetId());
        assertEquals(origin.planetId(), getDronePlanet(drone2));

        // ================================================================
        // 📋 PHASE 9: INSTALL 4 HYPERSPACE TUNNELS
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
        List<?> tunnels = getAllTunnels();
        assertEquals(4, tunnels.size());

        // ================================================================
        // 📋 PHASE 11: DRONE 1 - USE TUNNEL 1
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
        UUID tunnel2Entry = tunnelPlanets.get(2);
        UUID tunnel2Exit = tunnelPlanets.get(3);

        moveDroneTo(drone2, origin.planetId(), tunnel2Entry, planetMap);
        assertEquals(tunnel2Entry, getDronePlanet(drone2));

        sendCommand(drone2, "transport");
        assertEquals(tunnel2Exit, getDronePlanet(drone2));

        // ================================================================
        // 📋 PHASE 13: DRONES RETURN HOME
        // ================================================================
        List<Step> pathHome1 = findPath(tunnel1Exit, origin.planetId(), planetMap);
        assertFalse(pathHome1.isEmpty(), "No path home for drone 1");
        for (Step step : pathHome1) {
            sendCommand(drone1, step.direction());
            assertEquals(step.to(), getDronePlanet(drone1));
        }
        assertEquals(origin.planetId(), getDronePlanet(drone1));

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
        List<?> drone1History = getCommandHistory(drone1);
        List<?> drone2History = getCommandHistory(drone2);

        assertFalse(drone1History.isEmpty());
        assertFalse(drone2History.isEmpty());

        // ================================================================
        // 📋 PHASE 15: GET INDIVIDUAL DRONES
        // ================================================================
        getDrone(drone1);
        getDrone(drone2);

        // ================================================================
        // 📋 PHASE 16: GET ALL DRONES
        // ================================================================
        List<?> allDrones = getAllDrones();
        assertTrue(allDrones.size() >= 2);

        // ================================================================
        // 📋 PHASE 17: CLEAR DRONE 2 COMMAND HISTORY
        // ================================================================
        restTemplate.delete(baseUrl + "/explorationDrones/{droneId}/commands", drone2);
        assertTrue(getCommandHistory(drone2).isEmpty());

        // ================================================================
        // 📋 PHASE 18: SHUTDOWN ALL TUNNELS
        // ================================================================
        shutdownTunnel(tunnel1);
        shutdownTunnel(tunnel2);
        shutdownTunnel(tunnel3);
        shutdownTunnel(tunnel4);

        ResponseEntity<List<Object>> remainingTunnelsResponse = restTemplate.exchange(
                baseUrl + "/hyperspaceenergytunnels",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Object>>() {}
        );

        List<?> remainingTunnels = remainingTunnelsResponse.getBody();
        assertEquals(4, remainingTunnels.size(), "All 4 tunnels should still exist");
        for (Object tunnel : remainingTunnels) {
            Map<String, Object> tunnelMap = (Map<String, Object>) tunnel;
            assertEquals("INACTIVE", tunnelMap.get("tunnelState"),
                    "Tunnel should be INACTIVE after shutdown");
        }
    }

    // ================================================================
    // 🔧 HELPER METHODS (با RestTemplate به‌جای MockMvc)
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

    private void resetMap() {
        restTemplate.postForEntity(baseUrl + "/planets/reset", null, Void.class);
    }

    private void generateMap(int count) {
        restTemplate.postForEntity(baseUrl + "/planets/generate/" + count, null, Void.class);
    }

    private List<PlanetResponseDto> getAllPlanets() {
        ResponseEntity<List<PlanetResponseDto>> response = restTemplate.exchange(
                baseUrl + "/planets",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PlanetResponseDto>>() {}
        );
        return response.getBody();
    }

    private boolean isOrigin(PlanetResponseDto planet) {
        return PlanetType.SPACE_STATION.getValue().equals(planet.planetType());
    }

    // ================================================================
    // ⛏️ URANIUM OPERATIONS
    // ================================================================

    private void addUranium(UUID planetId, int amount) {
        UraniumRequestDto dto = new UraniumRequestDto();
        dto.setAmount(amount);
        restTemplate.postForEntity(
                baseUrl + "/planets/{planetId}/uraniums",
                dto,
                Void.class,
                planetId
        );
    }

    // ================================================================
    // 🛸 DRONE OPERATIONS
    // ================================================================

    private UUID spawnDrone() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/explorationDrones/spawn",
                null,
                Map.class
        );
        return UUID.fromString((String) response.getBody().get("id"));
    }

    private void sendCommand(UUID droneId, String command) {
        CommandRequestDto dto = new CommandRequestDto();
        dto.setCommandString(command);

        //System.out.println("COMMAND = >" + command + "<");

        restTemplate.postForEntity(
                baseUrl + "/explorationDrones/{droneId}/commands",
                dto,
                Void.class,
                droneId
        );
    }

    private UUID getDronePlanet(UUID droneId) {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/explorationDrones/{droneId}",
                Map.class,
                droneId
        );
        return UUID.fromString((String) response.getBody().get("planetId"));
    }

    private void getDrone(UUID droneId) {
        restTemplate.getForEntity(
                baseUrl + "/explorationDrones/{droneId}",
                Void.class,
                droneId
        );
    }

    private List<?> getCommandHistory(UUID droneId) {
        ResponseEntity<List<Object>> response = restTemplate.exchange(
                baseUrl + "/explorationDrones/{droneId}/commands",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Object>>() {},
                droneId
        );
        return response.getBody();
    }

    private List<?> getAllDrones() {
        ResponseEntity<List<Object>> response = restTemplate.exchange(
                baseUrl + "/explorationDrones",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Object>>() {}
        );
        return response.getBody();
    }

    // ================================================================
    // 🌉 TUNNEL OPERATIONS
    // ================================================================

    private UUID installTunnel(UUID entryPlanetId, UUID exitPlanetId) {
        HyperspaceEnergyTunnelRequestDto dto = new HyperspaceEnergyTunnelRequestDto();
        dto.setEntryPlanetId(entryPlanetId);
        dto.setExitPlanetId(exitPlanetId);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/hyperspaceenergytunnels",
                dto,
                Map.class
        );
        return UUID.fromString((String) response.getBody().get("id"));
    }

    private void shutdownTunnel(UUID tunnelId) {
        restTemplate.delete(baseUrl + "/hyperspaceenergytunnels/{tunnelId}/shutdown", tunnelId);
    }

    private List<?> getAllTunnels() {
        ResponseEntity<List<Object>> response = restTemplate.exchange(
                baseUrl + "/hyperspaceenergytunnels",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Object>>() {}
        );
        return response.getBody();
    }

    // ================================================================
    // 🧭 GRAPH / MOVEMENT
    // ================================================================

    private void moveDroneTo(UUID droneId, UUID start, UUID target, Map<UUID, PlanetResponseDto> planets) {
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