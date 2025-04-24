package com.neotee.exploration_drone_controller.regressionTests;

import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;
import certification.ExplorationDroneControl;
import certification.PlanetExamining;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.core.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
public class E0CompatibilityMovementTests {
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
    public void test0PlanetTypeOk() {
        
        // given
        UUID explorationDroneId = testHelper.performSpawn( explorationDroneControl );

        // when
        explorationDroneControl.executeCommand(
                Command.fromCommandString( "[north," + explorationDroneId.toString() + "]" ) );
        UUID planetId = explorationDroneControl.getExplorationDronePlanet( explorationDroneId );
        UUID planetId6 = testHelper.getPlanetId( 6 );
        UUID planetId11 = testHelper.getPlanetId( 11 );

        // then
        assertEquals( "regular", explorationDroneControl.getPlanetType( planetId ) );
        assertEquals( "unknown", explorationDroneControl.getPlanetType( planetId6 ) );
        assertEquals( "unknown", explorationDroneControl.getPlanetType( planetId11 ) );


    }


    @Test
    public void test1MoveAgainstBlock() {
        
        UUID explorationDroneId = testHelper.performActions( explorationDroneControl, "ccc;north;west;north;south;east;east;0" );
        testHelper.performActions( explorationDroneControl, "east;-1", explorationDroneId );

    }

    @Test
    public void test2MineMultipleTimes() {
        
        // given
        UUID explorationDroneId = testHelper.performActions( explorationDroneControl, "ccc;north;mmm;east;mmm;20" );

        // when
        UUID planetId = explorationDroneControl.getExplorationDronePlanet( explorationDroneId );

        // then
        assertEquals( Uranium.fromAmount( Integer.valueOf( "21" ) ),
                explorationDroneControl.getPlanetUraniumAmount( planetId ) );
    }

    @Test
    public void test3TwoExplorationDronesOnSamePlanet() {
        
        // given
        UUID explorationDroneIdA = testHelper.performActions( explorationDroneControl, "ccc;north;north;west;south;0" );
        UUID explorationDroneIdB = testHelper.performActions( explorationDroneControl, "ccc;north;west;0" );

        // when
        UUID planetIdA = explorationDroneControl.getExplorationDronePlanet( explorationDroneIdA );
        UUID planetIdB = explorationDroneControl.getExplorationDronePlanet( explorationDroneIdB );

        // then
        assertEquals( planetIdA, planetIdB );
        List<UUID> list = explorationDroneControl.getPlanetExplorationDrones( planetIdB );
        assertEquals( 2, list.size() );
    }
}
