package com.neotee.exploration_drone_controller.regressionTests;

import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;
import certification.ExplorationDroneControl;
import certification.ExplorationDroneControlException;
import certification.PlanetExamining;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.core.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
public class E0CompatibilityValidationTests {
    @Autowired
    private ExplorationDroneControl explorationDroneControl;
    @Autowired
    private PlanetExamining planetExamining;

    private TestHelper testHelper = new TestHelper();

    @BeforeEach
    public void setUp() {
        explorationDroneControl.resetAll();
    }

    @Test
    public void testExecuteCommandValidation() {
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.executeCommand( null );
        }, "Validation error expected for invalid command <null>");

        final String s1 = "";
        assertThrows( ExplorationDroneControlException.class, () -> {
            Command command = Command.fromCommandString( s1 );
            explorationDroneControl.executeCommand( command );
        }, "Validation error expected for invalid command " + s1 );

        final String s2 = "[eeast,554c00de-3a81-43e8-a384-2dcfebdcf34c]";
        assertThrows( ExplorationDroneControlException.class, () -> {
            Command command = Command.fromCommandString( s2 );
            explorationDroneControl.executeCommand( command );
        }, "Validation error expected for invalid command " + s2 );

        final String s3 = "[,554c00de-3a81-43e8-a384-2dcfebdcf34c]";
        assertThrows( ExplorationDroneControlException.class, () -> {
            Command command = Command.fromCommandString( s3 );
            explorationDroneControl.executeCommand( command );
        }, "Validation error expected for invalid command " + s3 );

        final String s4 = "[gohome,554c00de-3a81-43e8-a384-2dcfebdcf34c";
        assertThrows( ExplorationDroneControlException.class, () -> {
            Command command = Command.fromCommandString( s4 );
            explorationDroneControl.executeCommand( command );
        }, "Validation error expected for invalid command " + s4 );

        final String s5 = "[explore,000000-000-00]";
        assertThrows( ExplorationDroneControlException.class, () -> {
            Command command = Command.fromCommandString( s5 );
            explorationDroneControl.executeCommand( command );
        }, "Validation error expected for invalid command " + s5 );
    }






    @Test
    public void testGetExplorationDroneLoad() {
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.getExplorationDroneLoad( null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testGetExplorationDronePlanet() {
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.getExplorationDronePlanet( null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testGetPlanetType() {
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.getPlanetType( null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testGetPlanetUraniumAmount() {
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.getPlanetUraniumAmount( null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testGetPlanetExplorationDrones() {
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.getPlanetExplorationDrones( null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testNeighboursDetected() {
        assertThrows( ExplorationDroneControlException.class, () -> {
            planetExamining.neighboursDetected( null, null, null, null, null );
        }, "Validation error expected for invalid UUID <null>" );
    }

    @Test
    public void testUraniumDetected() {
        // given
        // when
        UUID neighbourId = testHelper.createNorthNeighbour( explorationDroneControl, planetExamining );

        // then
        assertThrows( ExplorationDroneControlException.class, () -> {
            planetExamining.uraniumDetected( null, Uranium.fromAmount( 1 ) );
        }, "Validation error expected for invalid UUID <null>" );

        assertThrows( ExplorationDroneControlException.class, () -> {
            planetExamining.uraniumDetected( neighbourId, null );
        }, "Validation error expected for invalid quantity <null>" );

        assertThrows( ExplorationDroneControlException.class, () -> {
            Uranium uranium = Uranium.fromAmount( -3 );
            planetExamining.uraniumDetected( neighbourId, uranium );
        }, "Validation error expected for invalid quantity -3" );
    }



}
