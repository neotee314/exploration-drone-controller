package com.neotee.exploration_drone_controller.regressionTests;

import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;
import certification.ExplorationDroneControl;
import certification.ExplorationDroneControlException;
import certification.PlanetExamining;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.core.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
public class E0CompatibilityBasicTests {
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
    public void testJustOnePlanetFoundAfterReset() {
        List<UUID> found = explorationDroneControl.getPlanets();
        assertEquals( 1, found.size() );
        String type = explorationDroneControl.getPlanetType( found.get( 0 ) );
        assertEquals( "space station", type );
    }

    @Test
    public void testNoMiningOnZero() {
        testHelper.performActions( explorationDroneControl, "ccc;mmm;-1" );
    }

    @Test
    public void testNoDoubleSpawn() {
        UUID explorationDroneId = testHelper.performSpawn( explorationDroneControl );
        assertThrows( ExplorationDroneControlException.class, () -> {
            testHelper.performSpawn( explorationDroneControl, explorationDroneId );
        }, "Error for double creation expected" );
    }

    @Test
    public void testNoDoubleMine() {
        // given
        UUID neighbourId = testHelper.createNorthNeighbour( explorationDroneControl, planetExamining );
        planetExamining.uraniumDetected( neighbourId, Uranium.fromAmount( 5 ) );
        UUID explorationDroneId = UUID.randomUUID();

        // when
        explorationDroneControl.executeCommand(
                Command.fromCommandString( "[spawn," + explorationDroneId.toString() + "]" ) );
        explorationDroneControl.executeCommand(
                Command.fromCommandString( "[north," + explorationDroneId.toString() + "]" ) );
        explorationDroneControl.executeCommand(
                Command.fromCommandString( "[mine," + explorationDroneId.toString() + "]" ) );
        assertEquals( Load.fromCapacityAndFilling( 20, Uranium.fromAmount( 5 ) ),
                explorationDroneControl.getExplorationDroneLoad( explorationDroneId ) );

        // then
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.executeCommand(
                    Command.fromCommandString( "[mine," + explorationDroneId.toString() + "]" ) );
        }, "Error for double mining expected" );
    }
}
