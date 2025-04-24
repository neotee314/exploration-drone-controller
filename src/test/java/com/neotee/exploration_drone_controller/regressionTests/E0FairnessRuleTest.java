package com.neotee.exploration_drone_controller.regressionTests;

import com.neotee.exploration_drone_controller.ExplorationDroneControllerApplication;
import certification.ExplorationDroneControl;
import certification.ExplorationDroneControlException;
import certification.PlanetExamining;
import com.neotee.exploration_drone_controller.domainprimitives.Command;
import com.neotee.exploration_drone_controller.core.TestHelper;
import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.dependency.DependencyAnalyzer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import static guru.nidi.codeassert.junit.CodeAssertCoreMatchers.hasNoCycles;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ExplorationDroneControllerApplication.class)
public class E0FairnessRuleTest {
   @Autowired
    private ExplorationDroneControl explorationDroneControl;
    @Autowired
    private PlanetExamining planetExamining;

    private TestHelper testHelper = new TestHelper();
    private final AnalyzerConfig config = AnalyzerConfig.maven().main();
    @BeforeEach
    public void setUp() {
        UUID originId = explorationDroneControl.resetAll();
        testHelper.setUpSector( planetExamining,
                "2,4,9,12", "1,5,7,8", "17,2,24,6",
                originId );
    }

    @Test
    public void noCycles() {
        assertThat(new DependencyAnalyzer(config).analyze(), hasNoCycles());
    }


    @Test
    public void test4TwoExplorationDronesMineOnSamePlanet() {
        // given
        UUID explorationDroneIdA = testHelper.performActions( explorationDroneControl, "ccc;north;west;north;south;east;east;0" );
        UUID explorationDroneIdB = testHelper.performActions( explorationDroneControl, "ccc;north;mmm;west;north;south;east;east;17" );

        // when --- only A is allowed to mine first, since it is empty
        assertThrows( ExplorationDroneControlException.class, () -> {
            explorationDroneControl.executeCommand(
                    Command.fromCommandString( "[mine," + explorationDroneIdB.toString() + "]" ) );
        }, "Error expected, as " + explorationDroneIdB + " is not the emptiest!" );
        testHelper.performActions( explorationDroneControl, "mmm;20", explorationDroneIdA );
    }

}
