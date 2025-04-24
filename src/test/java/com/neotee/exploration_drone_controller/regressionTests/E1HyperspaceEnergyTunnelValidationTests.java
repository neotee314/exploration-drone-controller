package com.neotee.exploration_drone_controller.regressionTests;

import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;
import certification.ExplorationDroneControl;
import certification.ExplorationDroneControlException;
import certification.PlanetExamining;
import com.neotee.exploration_drone_controller.core.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
public class E1HyperspaceEnergyTunnelValidationTests {
    @Autowired
    private ExplorationDroneControl explorationDroneControl;
    @Autowired
    private PlanetExamining planetExamining;

    private TestHelper testHelper = new TestHelper();
    private UUID originId;

    @BeforeEach
    public void setUp() {
        originId = explorationDroneControl.resetAll();
        testHelper.setUpSector( planetExamining,
                "2,4,9,12", "1,5,7,8", "17,2,24,6",
                originId );
    }

    @Test
    public void testInstallationValidation() {
        // given
        // when
        // then
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.installHyperspaceEnergyTunnel( null, null );
        }, "Validation error expected for null/null installation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.installHyperspaceEnergyTunnel( testHelper.getPlanetId( 1 ), null );
        }, "Validation error expected for null/id installation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.installHyperspaceEnergyTunnel( null, testHelper.getPlanetId( 3 ) );
        }, "Validation error expected for id/null installation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.installHyperspaceEnergyTunnel( testHelper.getPlanetId( 3 ), testHelper.getPlanetId( 3 ) );
        }, "Validation error expected for same planet installation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.installHyperspaceEnergyTunnel( testHelper.getPlanetId( 11 ), UUID.randomUUID() );
        }, "Validation error expected for same id/invalid id installation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.installHyperspaceEnergyTunnel( UUID.randomUUID(), testHelper.getPlanetId( 11 ) );
        }, "Validation error expected for same id/invalid id installation" );
    }


    @Test
    public void testRelocateValidation() {
        // given
        UUID hyperspaceEnergyTunnelId = explorationDroneControl.installHyperspaceEnergyTunnel(
                testHelper.getPlanetId( 3 ), testHelper.getPlanetId( 1 ) );

        // when
        // then
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.relocateHyperspaceEnergyTunnel( null, null, null );
        }, "Validation error expected for null/null/null relocation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.relocateHyperspaceEnergyTunnel( null, testHelper.getPlanetId( 11 ), null );
        }, "Validation error expected for null/null/id relocation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.relocateHyperspaceEnergyTunnel( null,testHelper.getPlanetId( 11 ), testHelper.getPlanetId( 6 ) );
        }, "Validation error expected for null/id/id relocation" );

        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.relocateHyperspaceEnergyTunnel( hyperspaceEnergyTunnelId, null, testHelper.getPlanetId( 11 ) );
        }, "Validation error expected for id/null/id relocation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.relocateHyperspaceEnergyTunnel( hyperspaceEnergyTunnelId, testHelper.getPlanetId( 11 ), testHelper.getPlanetId( 11 ) );
        }, "Validation error expected for same planet relocation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.relocateHyperspaceEnergyTunnel( hyperspaceEnergyTunnelId, testHelper.getPlanetId( 11 ), UUID.randomUUID() );
        }, "Validation error expected for same id/id/invalid id relocation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.relocateHyperspaceEnergyTunnel( hyperspaceEnergyTunnelId, UUID.randomUUID(), testHelper.getPlanetId( 11 ) );
        }, "Validation error expected for same id/invalid id relocation" );
    }

    @Test
    public void testShutdownValidation() {
        // given
        UUID hyperspaceEnergyTunnelId = explorationDroneControl.installHyperspaceEnergyTunnel(
                testHelper.getPlanetId( 6 ), testHelper.getPlanetId( 11 ) );

        // when
        // then
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.shutdownHyperspaceEnergyTunnel( null );
        }, "Validation error expected for null shutdown" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.shutdownHyperspaceEnergyTunnel( UUID.randomUUID() );
        }, "Validation error expected for invalid id shutdown" );
    }


    @Test
    public void testNoChangeAfterShutdown() {
        // given
        UUID hyperspaceEnergyTunnelId = explorationDroneControl.installHyperspaceEnergyTunnel(
                testHelper.getPlanetId( 1 ), testHelper.getPlanetId( 3 ) );

        // when
        explorationDroneControl.shutdownHyperspaceEnergyTunnel( hyperspaceEnergyTunnelId );

        // then
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.relocateHyperspaceEnergyTunnel( hyperspaceEnergyTunnelId, testHelper.getPlanetId( 3 ), testHelper.getPlanetId( 11 ) );
        }, "Validation error expected for shutdown relocation" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.shutdownHyperspaceEnergyTunnel( hyperspaceEnergyTunnelId );
        }, "Validation error expected for double shutdown" );
    }

    @Test
    public void testTwoStartingAtSamePlanet() {
        // given
        UUID hyperspaceEnergyTunnelId = explorationDroneControl.installHyperspaceEnergyTunnel(
                testHelper.getPlanetId( 1 ), testHelper.getPlanetId( 3 ) );

        // when that should be ok ...
        explorationDroneControl.relocateHyperspaceEnergyTunnel( hyperspaceEnergyTunnelId,
                testHelper.getPlanetId( 1 ), testHelper.getPlanetId( 11 ) );

        // then
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.installHyperspaceEnergyTunnel(
                    testHelper.getPlanetId( 1 ), testHelper.getPlanetId( 6 ) );
        }, "Validation error expected for double start" );
    }
}
