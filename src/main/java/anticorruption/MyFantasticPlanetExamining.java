package anticorruption;

import certification.PlanetExamining;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.planet.application.NeighbourPlanetService;
import com.neotee.exploration_drone_controller.planet.application.PlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.neotee.exploration_drone_controller.domainprimitives.CompassPoint.*;

@Service
@RequiredArgsConstructor
public class MyFantasticPlanetExamining implements PlanetExamining {

    private final PlanetService planetService;
    private final NeighbourPlanetService neighbourPlanetService;


    @Override
    public void neighboursDetected(UUID planetId, UUID northNeighbourOrNull, UUID eastNeighbourOrNull, UUID southNeighbourOrNull, UUID westNeighbourOrNull) {
        //create Planets
        neighbourPlanetService.createNeighborOf(planetId, northNeighbourOrNull, NORTH);
        neighbourPlanetService.createNeighborOf(planetId, eastNeighbourOrNull, EAST);
        neighbourPlanetService.createNeighborOf(planetId, southNeighbourOrNull, SOUTH);
        neighbourPlanetService.createNeighborOf(planetId, westNeighbourOrNull, WEST);

    }

    @Override
    public void uraniumDetected(UUID planetId, Uranium uranium) {
        planetService.addToUranium(planetId, uranium);
    }
}
