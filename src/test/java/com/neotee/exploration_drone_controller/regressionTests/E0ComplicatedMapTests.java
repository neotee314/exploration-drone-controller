package com.neotee.exploration_drone_controller.regressionTests;

import certification.ExplorationDroneControl;
import certification.PlanetExamining;
import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;
import com.neotee.exploration_drone_controller.planet.application.PlanetService;
import com.neotee.exploration_drone_controller.planet.domain.Planet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static com.neotee.exploration_drone_controller.domainprimitives.CompassPoint.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
public class E0ComplicatedMapTests {

    @Autowired
    private PlanetExamining planetExamining;
    @Autowired
    private ExplorationDroneControl explorationDroneControl;
    @Autowired
    private PlanetService planetService;

    @BeforeEach
    void setUp() {
        explorationDroneControl.resetAll();
    }

    @Test
    public void spaceStationShouldHaveNorthNeighbour() {
        UUID spaceStationId = explorationDroneControl.getPlanets().getFirst();

        String path = "swwseeeeeeennenenwnwwwwwwwseesw";

        UUID northNeighbourId = null;
        UUID southNeighbourId = null;
        UUID eastNeighbourId = null;
        UUID westNeighbourId = null;

        UUID currentPlanetId = spaceStationId;

        for (char direction : path.toCharArray()) {
            UUID newPlanetId = UUID.randomUUID();

            if (direction == 'n') {
                northNeighbourId = newPlanetId;
                planetExamining.neighboursDetected(currentPlanetId, northNeighbourId, eastNeighbourId, southNeighbourId, westNeighbourId);
                currentPlanetId = northNeighbourId;
                northNeighbourId = null;
            } else if (direction == 's') {
                southNeighbourId = newPlanetId;
                planetExamining.neighboursDetected(currentPlanetId, northNeighbourId, eastNeighbourId, southNeighbourId, westNeighbourId);
                currentPlanetId = southNeighbourId;
                southNeighbourId = null;
            } else if (direction == 'e') {
                eastNeighbourId = newPlanetId;
                planetExamining.neighboursDetected(currentPlanetId, northNeighbourId, eastNeighbourId, southNeighbourId, westNeighbourId);
                currentPlanetId = eastNeighbourId;
                eastNeighbourId = null;
            } else if (direction == 'w') {
                westNeighbourId = newPlanetId;
                planetExamining.neighboursDetected(currentPlanetId, northNeighbourId, eastNeighbourId, southNeighbourId, westNeighbourId);
                currentPlanetId = westNeighbourId;
                westNeighbourId = null;
            }
        }

        Planet spaceStation = planetService.findPlanetById(spaceStationId);

        assertNotNull(spaceStation.getNeighbourOf(NORTH), "SpaceStation should have a north neighbour");
    }
}
