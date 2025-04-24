package com.neotee.exploration_drone_controller.regressionTests;


import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;

import com.neotee.exploration_drone_controller.domainprimitives.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.context.WebApplicationContext;
import thkoeln.st.springtestlib.relation.GenericAggregateTests;
import thkoeln.st.springtestlib.validation.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
public class E0DomainPrimitiveFunctionalTests {
    private GenericAggregateTests genericAggregateTests;
    private GenericValueObjectTests genericValueObjectTests;
    private GenericWrapperClassTests genericWrapperClassTests;


    @Autowired
    public E0DomainPrimitiveFunctionalTests(WebApplicationContext appContext ) {
        genericAggregateTests = new GenericAggregateTests( appContext );
        genericValueObjectTests = new GenericValueObjectTests();
        genericWrapperClassTests = new GenericWrapperClassTests();
    }

    // todo - the commented-out part is just taken over from SS21 milestone. Needs to be rewritten, with
    // a similar structure.

    // ORDER (Command)
    @Test
    public void createdCommandFromStringTest() {
        Command compassPointMoveCommand = Command.fromCommandString("[north,137b8ef4-cae4-11ec-9d64-0242ac120002]");
        Command spawnCommand = Command.fromCommandString("[spawn,137b8ef4-cae4-11ec-9d64-0242ac120002]");
        Command mineCommand = Command.fromCommandString("[mine,137b8ef4-cae4-11ec-9d64-0242ac120002]");

        assertTrue(compassPointMoveCommand.isMove());
        assertTrue(spawnCommand.isSpawn());
        assertTrue(mineCommand.isMine());
    }
    // TODO test more Command functionalities

    // CARGO (Load)
    @Test
    public void fillLoadWithValidUraniumTest() {
        Load load = Load.fromCapacity(42);
        Uranium uraniumA = Uranium.fromAmount(21);
        Uranium uraniumB = Uranium.fromAmount(69);

        Load loadWithEmptySpace = load.fillFrom(uraniumA);
        Load fullLoad = load.fillFrom(uraniumB);

        assertEquals(21, loadWithEmptySpace.availableCapacity().getAmount());
        assertEquals(0, fullLoad.availableCapacity().getAmount());
    }

    @Test
    public void leaveBehindUraniumWithValidUraniumTest() {
        int capacity = 42;
        int uraniumAmount = 69;
        int expectedLeftovers = uraniumAmount - capacity;
        Load load = Load.fromCapacity(42);
        Uranium richUranium = Uranium.fromAmount(69);

        Uranium leftoverUranium = load.leaveBehindWhenFillingFrom(richUranium);

        assertEquals(expectedLeftovers, leftoverUranium.getAmount());
    }

    @Test
    public void loadHasMoreFreeCapacityThanOtherFailureTest() {
        Uranium uraniumA = Uranium.fromAmount(30);
        Uranium uraniumB = Uranium.fromAmount(40);
        Load loadA = Load.fromCapacity(70);
        Load loadB = Load.fromCapacity(50);
        loadA = loadA.fillFrom(uraniumA);
        loadB = loadB.fillFrom(uraniumB);

        assertTrue(loadA.hasMoreFreeCapacityThan(loadB));
    }

    // COMPASS_DIRECTION (CompassPoint)
    @Test
    public void compassPointOppositeDirectionTest() {
        assertEquals(CompassPoint.SOUTH, CompassPoint.NORTH.oppositeDirection());
        assertEquals(CompassPoint.NORTH, CompassPoint.SOUTH.oppositeDirection());
        assertEquals(CompassPoint.WEST, CompassPoint.EAST.oppositeDirection());
        assertEquals(CompassPoint.EAST, CompassPoint.WEST.oppositeDirection());
    }

    // MINEABLE_RESOURCE (Uranium)
    @Test
    public void uraniumFromAmountTest() {
        int amount = 10;
        Uranium uranium = Uranium.fromAmount(amount);

        assertEquals(amount, uranium.getAmount());
    }

    // MINEABLE_RESOURCE (Uranium)
    @Test
    public void uraniumCompareTest() {
        Uranium uranium = Uranium.fromAmount(10);
        Uranium equal = Uranium.fromAmount(10);
        Uranium lesser = Uranium.fromAmount(5);
        Uranium greater = Uranium.fromAmount(50);
        Uranium empty = Uranium.fromAmount(0);

        assertTrue(empty.isZero());
        assertFalse(uranium.isZero());

        assertTrue(uranium.isGreaterEqualThan(equal));
        assertTrue(uranium.isGreaterEqualThan(lesser));
        assertFalse(uranium.isGreaterEqualThan(greater));

        assertTrue(uranium.isGreaterThan(lesser));
        assertFalse(uranium.isGreaterThan(equal));
        assertFalse(uranium.isGreaterThan(greater));
    }

    @Test
    public void uraniumValueChangeTest() {
        Uranium uraniumA = Uranium.fromAmount(10);
        Uranium uraniumB = Uranium.fromAmount(25);

        assertEquals(35, uraniumA.addTo(uraniumB).getAmount());
        assertEquals(15, uraniumA.subtractFrom(uraniumB).getAmount());
        assertEquals(0, uraniumB.subtractFrom(uraniumA).getAmount());
    }



    @Test
    public void testCompassPointPathAddAndBacktrack() {
        // given
        CompassPointPath path = CompassPointPath.empty();
        assertEquals( 0, path.length() );

        // when
        path = path.addMovement( CompassPoint.NORTH );
        path = path.addMovement( CompassPoint.WEST );
        path = path.addMovement( CompassPoint.EAST );
        path = path.addMovement( CompassPoint.EAST );

        // then
        assertEquals( 4, path.length() );
        Assertions.assertEquals( CompassPoint.WEST, path.directionToGoBackTo() );
        path = path.backtrackLastMovement();
        Assertions.assertEquals( CompassPoint.WEST, path.directionToGoBackTo() );
        path = path.backtrackLastMovement();
        Assertions.assertEquals( CompassPoint.EAST, path.directionToGoBackTo() );
        assertEquals( 2, path.length() );
        path = path.backtrackLastMovement();
        Assertions.assertEquals( CompassPoint.SOUTH, path.directionToGoBackTo() );
    }

}
