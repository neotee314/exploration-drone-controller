package com.neotee.exploration_drone_controller.core;



import certification.ExplorationDroneControl;
import certification.ExplorationDroneControlException;
import certification.PlanetExamining;
import com.neotee.exploration_drone_controller.domainprimitives.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestHelper {
    private static final int MAX_CELLS = 16;
    private UUID[] cells = new UUID[MAX_CELLS];



    public UUID performSpawn( ExplorationDroneControl explorationDroneControl ) {
        UUID explorationDroneId = UUID.randomUUID();
        performSpawn( explorationDroneControl, explorationDroneId );
        return explorationDroneId;
    }

    public void performSpawn( ExplorationDroneControl explorationDroneControl, UUID explorationDroneId ) {
        String commandString = "[spawn," + String.valueOf( explorationDroneId ) + "]";
        Command command = Command.fromCommandString( commandString );
        explorationDroneControl.executeCommand( command );
    }

    public UUID performActions( ExplorationDroneControl explorationDroneControl, String CommandSequence ) {
        UUID explorationDroneId = UUID.randomUUID();
        performActions( explorationDroneControl, CommandSequence, explorationDroneId );
        return explorationDroneId;
    }

    public UUID createNorthNeighbour(ExplorationDroneControl explorationDroneControl, PlanetExamining planetExamining ) {
        UUID originId = explorationDroneControl.getPlanets().get( 0 );
        UUID neighBourId = UUID.randomUUID();
        planetExamining.neighboursDetected( originId, neighBourId, null, null, null );
        return neighBourId;
    }

    public void performActions( ExplorationDroneControl explorationDroneControl, String commandSequence, UUID explorationDroneId ) {
        String[] commandArray = commandSequence.split( ";" );
        for ( int i = 0; i < commandArray.length - 1; i++ ) {
            final String commandString = createCommandString( commandArray[i], explorationDroneId );
            final Command command = Command.fromCommandString( commandString );
            if ( i < commandArray.length - 2 ) {
                explorationDroneControl.executeCommand( command );
            } else {
                Integer intload = Integer.valueOf( commandArray[commandArray.length-1] );
                if ( intload < 0 ) {
                    // -1 means "energy field" => Exception expected
                    assertThrows( ExplorationDroneControlException.class, () -> {
                        explorationDroneControl.executeCommand( command );
                    });
                }
                else {
                    explorationDroneControl.executeCommand( command );
                    Load load = Load.fromCapacityAndFilling( 20, Uranium.fromAmount( intload ) );
                    assertEquals( load, explorationDroneControl.getExplorationDroneLoad( explorationDroneId ) );
                }
            }
        }
    }

    private String createCommandString( String fragment, UUID explorationDroneId ) {
        String action;
        if ( fragment.equals( "mmm" ) ) {
            action = "mine";
        }
        else if ( fragment.equals( "ccc" ) ) {
            action = "spawn";
        }
        else {
            action = fragment;
        }
        return "[" + action + "," + String.valueOf( explorationDroneId ) + "]";
    }

    /**
     * To make this a little easier for you to debug: This method is creating a small sector
     * that you find depicted in src/test/resources/Grid.png (small numbers = cell numbers,
     * large numbers = uranium, red cell = space station, grey cells = "holes"
     */
    public void setUpSector( PlanetExamining planetExamining,
                                    String holes, String minCells, String minAmounts,
                                    UUID originId ) {
        cells[0] = originId;
        for ( int i = 1; i < MAX_CELLS; i++ ) {
            cells[i] = UUID.randomUUID();
        }

        // Hole = no planet
        Integer[] holesArray = decode4ElementIntArray( holes );
        for ( int i = 0; i < 4; i++ ) {
            cells[ holesArray[i] ] = null;
        }

        // see Excel maps and numbering there
        planetExamining.neighboursDetected( cells[0], cells[1], cells[2], cells[3], cells[4] );
        planetExamining.neighboursDetected( cells[1], cells[6], cells[7], cells[0], cells[5] );
        planetExamining.neighboursDetected( cells[6], null, cells[9], cells[1], cells[8] );
        planetExamining.neighboursDetected( cells[3], cells[0], cells[10], cells[11], cells[12] );
        planetExamining.neighboursDetected( cells[11], cells[3], cells[13], cells[14], cells[15] );

        // indeces, where uranium is, and the corresponding amount
        Integer[] minCellsArray = decode4ElementIntArray( minCells );
        Integer[] minAmountsArray = decode4ElementIntArray( minAmounts );
        for ( int i = 0; i < 4; i++ ) {
            Uranium uranium = Uranium.fromAmount( minAmountsArray[i] );
            planetExamining.uraniumDetected( cells[ minCellsArray[i] ], uranium );
        }
    }



    private Integer[] decode4ElementIntArray( String arrayString ) {
        Integer[] intArray = new Integer[4];
        String[] numberStrings = arrayString.split( "," );
        for ( int i = 0; i < 4; i++ ) {
            intArray[i] = Integer.valueOf( numberStrings[i] );
        }
        return intArray;
    }


    public UUID getPlanetId( int i ) {
        return cells[i];
    }

}
