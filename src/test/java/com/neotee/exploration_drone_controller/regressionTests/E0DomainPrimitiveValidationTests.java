package com.neotee.exploration_drone_controller.regressionTests;


import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;
import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPointPath;
import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
public class E0DomainPrimitiveValidationTests {


    // Command
    @Test
    public void commandFailedParsingTest() {
        assertThrows(ExplorationDroneControlException.class, () -> Command.fromCommandString("[north,4]"));
        assertThrows(ExplorationDroneControlException.class, () -> Command.fromCommandString("[1a553f9a-de70-4587-94b4-82205ae51ff2, west]"));
        assertThrows(ExplorationDroneControlException.class, () -> Command.fromCommandString("[[south,1a553f9a-de70-4587-94b4-82205ae51ff2]]"));
        assertThrows(ExplorationDroneControlException.class, () -> Command.fromCommandString("(east,1a553f9a-de70-4587-94b4-82205ae51ff2)"));

        assertThrows(ExplorationDroneControlException.class, () -> Command.fromCommandString("[spawn,invalidUUID]"));

        assertThrows(ExplorationDroneControlException.class, () -> Command.fromCommandString("[mine,invalidUUID]"));

        // invalid
        assertThrows(ExplorationDroneControlException.class, () -> Command.fromCommandString("[doStuff,42]"));
        Command command;
        assertThrows( ExplorationDroneControlException.class, () -> {
            Command.fromCommandString( "[nupf,1a553f9a-de70-4587-94b4-82205ae51ff2]" );
        }, "No appropriate validation exception thrown" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            Command.fromCommandString( "[,1a553f9a-de70-4587-94b4-82205ae51ff2]" );
        }, "No appropriate validation exception thrown" );
        assertThrows( ExplorationDroneControlException.class, () -> {
            Command.fromCommandString( "west,1a553f9a-de70-4587-94b4-82205ae51ff2]" );
        }, "No appropriate validation exception thrown" );

    }


    @Test
    public void loadCreatedFailingTest() {
        assertThrows( ExplorationDroneControlException.class, () -> Load.fromCapacity(null),
                "No null check exception!" );
        assertThrows( ExplorationDroneControlException.class, () -> Load.fromCapacity(-1),
                "No validation exception for negative capacity!" );
        assertThrows( ExplorationDroneControlException.class,
                () -> Load.fromCapacityAndFilling(30, null ),
                "No validation exception for null filling!" );
        assertThrows( ExplorationDroneControlException.class,
                () -> Load.fromCapacityAndFilling(10, Uranium.fromAmount( 15 ) ),
                "No validation exception for filling > capacity!" );
    }

    @Test
    public void fillLoadWithInvalidUraniumTest() {
        Load load = Load.fromCapacity(42);
        Uranium uranium = Uranium.fromAmount(0);
        assertThrows(ExplorationDroneControlException.class, () -> load.fillFrom(null));
        assertThrows(ExplorationDroneControlException.class, () -> load.fillFrom(uranium));
    }

    @Test
    public void leaveBehindResourceWithInvalidUraniumTest() {
        Load load = Load.fromCapacity(42);
        Uranium emptyUranium = Uranium.fromAmount(0);

        assertThrows(
                ExplorationDroneControlException.class,
                () -> load.leaveBehindWhenFillingFrom(null)
        );
        assertThrows(
                ExplorationDroneControlException.class,
                () -> load.leaveBehindWhenFillingFrom(emptyUranium)
        );
    }

    @Test
    public void loadHasMoreFreeCapacityThanOtherFailureTest() {
        Load load = Load.fromCapacity(42);
        assertThrows(ExplorationDroneControlException.class, () -> load.hasMoreFreeCapacityThan(null));
    }

    // Uranium
    @Test
    public void uraniumFromAmountFailureTest() {
        assertThrows(ExplorationDroneControlException.class, () -> Uranium.fromAmount(null));
        assertThrows(ExplorationDroneControlException.class, () -> Uranium.fromAmount(-1));
    }

    @Test
    public void uraniumCompareFailureTest() {
        Uranium uranium = Uranium.fromAmount(10);

        assertThrows(ExplorationDroneControlException.class, () -> uranium.isGreaterThan(null));
        assertThrows(ExplorationDroneControlException.class, () -> uranium.isGreaterEqualThan(null));
    }

    @Test
    public void UraniumValueChangeFailureTest() {
        Uranium uranium = Uranium.fromAmount(10);

        assertThrows(ExplorationDroneControlException.class, () -> uranium.addTo(null));
        assertThrows(ExplorationDroneControlException.class, () -> uranium.subtractFrom(null));
    }



    // CompassPointPath
    @Test
    public void testNoActionsForEmptyCompassPointPath() {
        assertThrows( ExplorationDroneControlException.class, () -> {
            CompassPointPath.empty().directionToGoBackTo();
        });
        assertThrows( ExplorationDroneControlException.class, () -> {
            CompassPointPath.empty().backtrackLastMovement();
        });
    }
}
