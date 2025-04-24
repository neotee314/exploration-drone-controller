package com.neotee.exploration_drone_controller.regressionTests;


import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;
import com.neotee.exploration_drone_controller.domainprimitives.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.lang.reflect.Method;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
public class E0DomainPrimitiveRulesTests {
    private Command command;
    private CompassPointPath compassPointPath;
    private Load load;
    private Uranium uranium;




    @BeforeEach
    public void setUp() {
        command = Command.fromCommandString( "[north,1a553f9a-de70-4587-94b4-82205ae51ff2]" );
        compassPointPath = CompassPointPath.empty();
        load = Load.fromCapacity( 12 );
        uranium = Uranium.fromAmount( 27 );
    }


    @Test
    public void noSetter() throws Exception {
        assertThrows( Exception.class, () -> {
            Method setter = command.getClass().getDeclaredMethod( "setExplorationDroneId" );
            setter.invoke( command, UUID.randomUUID() );
        }, "Did not expect that setExplorationDroneId could be called on " + command );
        assertThrows( Exception.class, () -> {
            Method setter = command.getClass().getDeclaredMethod( "setCompassPoint" );
            setter.invoke( command, CompassPoint.EAST );
        }, "Did not expect that setCompassPoint could be called on " + command );
        assertThrows( Exception.class, () -> {
            Method setter = compassPointPath.getClass().getDeclaredMethod( "setLength" );
            setter.invoke( compassPointPath, 4 );
        }, "Did not expect that setLength could be called on " + compassPointPath );
        assertThrows( Exception.class, () -> {
            Method setter = load.getClass().getDeclaredMethod( "setCapacity" );
            setter.invoke( load, 4 );
        }, "Did not expect that setCapacity could be called on " + load );
        assertThrows( Exception.class, () -> {
            Method setter = uranium.getClass().getDeclaredMethod( "setAmount" );
            setter.invoke( uranium, 4 );
        }, "Did not expect that setAmount could be called on " + uranium );
    }

}
