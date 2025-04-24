package com.neotee.exploration_drone_controller.regressionTests;

import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;
import certification.ExplorationDroneControl;
import certification.ExplorationDroneControlException;
import certification.PlanetExamining;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.core.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
public class E2NewCommandTests {
    @Autowired
    private ExplorationDroneControl explorationDroneControl;
    @Autowired
    private PlanetExamining planetExamining;

    private TestHelper testHelper = new TestHelper();

    @BeforeEach
    public void setUp() {
        UUID originId = explorationDroneControl.resetAll();
        testHelper.setUpSector(planetExamining,
                "2,4,9,12", "1,5,7,8", "17,2,24,6",
                originId);
    }

    @Test
    public void commandValidationTest() {
        assertThrows(ExplorationDroneControlException.class, () -> {
            Command.fromCommandString("[transport,]");
        }, "No appropriate validation exception thrown");
        assertThrows(ExplorationDroneControlException.class, () -> {
            Command.fromCommandString("[transport,1-1-1]");
        }, "No appropriate validation exception thrown");

        assertThrows(ExplorationDroneControlException.class, () -> {
            Command.fromCommandString("[explore,]");
        }, "No appropriate validation exception thrown");
        assertThrows(ExplorationDroneControlException.class, () -> {
            Command.fromCommandString("[explore,1-1-1]");
        }, "No appropriate validation exception thrown");

        assertThrows(ExplorationDroneControlException.class, () -> {
            Command.fromCommandString("[gohome,]");
        }, "No appropriate validation exception thrown");
        assertThrows(ExplorationDroneControlException.class, () -> {
            Command.fromCommandString("[gohome,1-1-1]");
        }, "No appropriate validation exception thrown");

    }


    @Test
    public void testExploreAndGoHome() {
        // given
        UUID explorationDroneId = testHelper.performSpawn(explorationDroneControl);
        explorationDroneControl.executeCommand(
                Command.fromCommandString("[explore," + explorationDroneId.toString() + "]"));
        UUID planetId = explorationDroneControl.getExplorationDronePlanet(explorationDroneId);
        assertNotEquals("space station", explorationDroneControl.getPlanetType(planetId));

        // when --- four more explorations, and five times go home ...
        for (int i = 0; i < 4; i++) {
            explorationDroneControl.executeCommand(
                    Command.fromCommandString("[explore," + explorationDroneId.toString() + "]"));
        }
        for (int i = 0; i < 5; i++) {
            // wrap in ExplorationDroneControlException, in case exploration drone arrives "early" at space station
            try {
                explorationDroneControl.executeCommand(
                        Command.fromCommandString("[gohome," + explorationDroneId.toString() + "]"));
            } catch (ExplorationDroneControlException e) {
            }
        }

        // then --- must be back on space station
        planetId = explorationDroneControl.getExplorationDronePlanet(explorationDroneId);

        assertEquals("space station", explorationDroneControl.getPlanetType(planetId));
    }

    @Test
    public void testTransport() {
        // given
        UUID explorationDroneId = testHelper.performSpawn(explorationDroneControl);


        UUID hyperspaceEnergyTunnelId1 = explorationDroneControl.installHyperspaceEnergyTunnel(
                testHelper.getPlanetId(1), testHelper.getPlanetId(11));
        UUID hyperspaceEnergyTunnelId2 = explorationDroneControl.installHyperspaceEnergyTunnel(
                testHelper.getPlanetId(11), testHelper.getPlanetId(0));

        // when
        explorationDroneControl.executeCommand(
                Command.fromCommandString("[north," + explorationDroneId.toString() + "]"));

        explorationDroneControl.executeCommand(
                Command.fromCommandString("[transport," + explorationDroneId.toString() + "]"));
        explorationDroneControl.executeCommand(
                Command.fromCommandString("[transport," + explorationDroneId.toString() + "]"));

        // then
        UUID planetId = explorationDroneControl.getExplorationDronePlanet(explorationDroneId);
        String type = explorationDroneControl.getPlanetType(planetId);
        assertEquals("space station", type);
    }

    @Test
    public void testTransportWithNoHyperspaceEnergyTunnel() {
        // given
        UUID explorationDroneId = testHelper.performSpawn(explorationDroneControl);

        // when
        explorationDroneControl.executeCommand(
                Command.fromCommandString("[north," + explorationDroneId.toString() + "]"));
        explorationDroneControl.executeCommand(
                Command.fromCommandString("[explore," + explorationDroneId.toString() + "]"));

        // then
        assertThrows(ExplorationDroneControlException.class, () -> {
            explorationDroneControl.executeCommand(
                    Command.fromCommandString("[transport," + explorationDroneId.toString() + "]"));
        }, "Validation error expected for invalid transport");
    }


    @Test
    public void testNoGoHomeAfterHyperspaceEnergyTunnel() {
        // given
        UUID explorationDroneId = testHelper.performSpawn(explorationDroneControl);
        UUID hyperspaceEnergyTunnelId1 = explorationDroneControl.installHyperspaceEnergyTunnel(
                testHelper.getPlanetId(1), testHelper.getPlanetId(11));

        // when
        explorationDroneControl.executeCommand(
                Command.fromCommandString("[north," + explorationDroneId.toString() + "]"));
        explorationDroneControl.executeCommand(
                Command.fromCommandString("[transport," + explorationDroneId.toString() + "]"));

        // then
        explorationDroneControl.executeCommand(
                Command.fromCommandString("[north," + explorationDroneId.toString() + "]"));
        assertThrows(ExplorationDroneControlException.class, () -> {
            explorationDroneControl.executeCommand(
                    Command.fromCommandString("[gohome," + explorationDroneId.toString() + "]"));
        }, "Error expected for going home after transport");
    }

}
