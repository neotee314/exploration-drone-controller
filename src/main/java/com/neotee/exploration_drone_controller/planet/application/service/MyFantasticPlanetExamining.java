package com.neotee.exploration_drone_controller.planet.application.service;

import com.neotee.exploration_drone_controller.certification.PlanetExamining;
import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.neotee.exploration_drone_controller.domainprimitives.CompassPoint.*;

@Service
@RequiredArgsConstructor
public class MyFantasticPlanetExamining implements PlanetExamining {

    private final PlanetApplicationService planetApplicationService;


    @Override
    public void neighboursDetected(UUID planetId, UUID northNeighbourOrNull, UUID eastNeighbourOrNull, UUID southNeighbourOrNull, UUID westNeighbourOrNull) {

        var planetIdValue = PlanetId.of(planetId);

        var northId = northNeighbourOrNull == null
                ? null
                : PlanetId.of(northNeighbourOrNull);

        var eastId = eastNeighbourOrNull == null
                ? null
                : PlanetId.of(eastNeighbourOrNull);

        var southId = southNeighbourOrNull == null
                ? null
                : PlanetId.of(southNeighbourOrNull);

        var westId = westNeighbourOrNull == null
                ? null
                : PlanetId.of(westNeighbourOrNull);

        planetApplicationService.createNeighborOf(planetIdValue, northId, NORTH);
        planetApplicationService.createNeighborOf(planetIdValue, eastId, EAST);
        planetApplicationService.createNeighborOf(planetIdValue, southId, SOUTH);
        planetApplicationService.createNeighborOf(planetIdValue, westId, WEST);
    }

    @Override
    public void uraniumDetected(UUID planetId, Uranium uranium) {
        var planetID = PlanetId.of(planetId);
        planetApplicationService.addToUranium(planetID, uranium);
    }
}
