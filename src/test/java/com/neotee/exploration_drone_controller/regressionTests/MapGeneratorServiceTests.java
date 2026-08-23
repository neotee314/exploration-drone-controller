package com.neotee.exploration_drone_controller.regressionTests;

import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;
import com.neotee.exploration_drone_controller.planet.application.service.MapGeneratorService;
import com.neotee.exploration_drone_controller.planet.domain.model.Planet;
import com.neotee.exploration_drone_controller.planet.domain.model.SpaceStation;
import com.neotee.exploration_drone_controller.planet.domain.repository.PlanetRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
@Transactional
class MapGeneratorServiceTests {

    @Autowired
    private MapGeneratorService mapGeneratorService;

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        planetRepository.deleteAll();
        entityManager.flush();
    }

    @Test
    void generateMapWithOnePlanetCreatesOnlySpaceStation() {

        Planet spaceStation =
                mapGeneratorService.generateMap(1, false);

        assertNotNull(spaceStation);
        assertTrue(spaceStation instanceof SpaceStation);

        List<Planet> planets = planetRepository.findAll();

        assertEquals(1, planets.size());
        assertTrue(planets.get(0) instanceof SpaceStation);
    }

    @Test
    void generateMapCreatesRequestedNumberOfPlanets() {

        int planetCount = 20;

        Planet spaceStation =
                mapGeneratorService.generateMap(planetCount, false);

        assertNotNull(spaceStation);

        List<Planet> planets = planetRepository.findAll();

        assertEquals(planetCount, planets.size());

        long spaceStationCount = planets.stream()
                .filter(SpaceStation.class::isInstance)
                .count();

        assertEquals(1, spaceStationCount);
    }

    @Test
    void generatedMapIsConnected() {

        int planetCount = 30;

        Planet spaceStation =
                mapGeneratorService.generateMap(planetCount, false);

        List<Planet> planets = planetRepository.findAll();

        assertEquals(planetCount, planets.size());

        /*
         * Because every newly generated planet is attached
         * to an already existing planet, every planet must
         * have at least one neighbour.
         *
         * The space station is the root of the generated graph.
         */
        for (Planet planet : planets) {

            boolean hasNeighbour =
                    planet.getNeighbourOf(
                            com.neotee.exploration_drone_controller
                                    .domainprimitives.CompassPoint.NORTH
                    ) != null
                    ||
                    planet.getNeighbourOf(
                            com.neotee.exploration_drone_controller
                                    .domainprimitives.CompassPoint.SOUTH
                    ) != null
                    ||
                    planet.getNeighbourOf(
                            com.neotee.exploration_drone_controller
                                    .domainprimitives.CompassPoint.EAST
                    ) != null
                    ||
                    planet.getNeighbourOf(
                            com.neotee.exploration_drone_controller
                                    .domainprimitives.CompassPoint.WEST
                    ) != null;

            assertTrue(
                    hasNeighbour,
                    "Every generated planet except the single-planet map " +
                    "must have at least one neighbour"
            );
        }
    }

    @Test
    void completePathsCreatesAllLogicalNeighbourConnections() {

        Planet spaceStation =
                mapGeneratorService.generateMap(50, true);

        List<Planet> planets = planetRepository.findAll();

        assertEquals(50, planets.size());

        /*
         * For every existing neighbour relation:
         *
         * A -- NORTH --> B
         *
         * B must have:
         *
         * SOUTH --> A
         *
         * addNeighbour() is expected to maintain this invariant.
         */
        for (Planet planet : planets) {

            assertSymmetric(
                    planet,
                    com.neotee.exploration_drone_controller
                            .domainprimitives.CompassPoint.NORTH,
                    com.neotee.exploration_drone_controller
                            .domainprimitives.CompassPoint.SOUTH
            );

            assertSymmetric(
                    planet,
                    com.neotee.exploration_drone_controller
                            .domainprimitives.CompassPoint.SOUTH,
                    com.neotee.exploration_drone_controller
                            .domainprimitives.CompassPoint.NORTH
            );

            assertSymmetric(
                    planet,
                    com.neotee.exploration_drone_controller
                            .domainprimitives.CompassPoint.EAST,
                    com.neotee.exploration_drone_controller
                            .domainprimitives.CompassPoint.WEST
            );

            assertSymmetric(
                    planet,
                    com.neotee.exploration_drone_controller
                            .domainprimitives.CompassPoint.WEST,
                    com.neotee.exploration_drone_controller
                            .domainprimitives.CompassPoint.EAST
            );
        }
    }

    private void assertSymmetric(
            Planet planet,
            com.neotee.exploration_drone_controller.domainprimitives.CompassPoint direction,
            com.neotee.exploration_drone_controller.domainprimitives.CompassPoint opposite
    ) {

        Planet neighbour = planet.getNeighbourOf(direction);

        if (neighbour == null) {
            return;
        }

        assertSame(
                planet,
                neighbour.getNeighbourOf(opposite),
                "Neighbour relation must be symmetric"
        );
    }

    @Test
    void generatedPlanetsHaveUraniumAmountBetweenZeroAndHundred() {

        mapGeneratorService.generateMap(50, false);

        List<Planet> planets = planetRepository.findAll();

        planets.stream()
                .filter(planet -> !(planet instanceof SpaceStation))
                .forEach(planet -> {

                    assertNotNull(planet.getUranium());

                    int amount =
                            planet.getUranium().getAmount();

                    assertTrue(
                            amount >= 0 && amount <= 100,
                            "Generated uranium must be between 0 and 100"
                    );
                });
    }

    @Test
    void invalidPlanetCountIsRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> mapGeneratorService.generateMap(0, false)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> mapGeneratorService.generateMap(-1, false)
        );
    }
}