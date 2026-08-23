package com.neotee.exploration_drone_controller.planet.domain.model;

import com.neotee.exploration_drone_controller.domainprimitives.PlanetId;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.UUID;

import static com.neotee.exploration_drone_controller.domainprimitives.PlanetType.SPACE_STATION;
import static com.neotee.exploration_drone_controller.domainprimitives.PlanetType.UNKNOWN;
import static com.neotee.exploration_drone_controller.domainprimitives.PlanetVisitStatus.NOT_VISITED;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class SpaceStation extends Planet {


    private SpaceStation(PlanetId planetId) {
        super(planetId);
        setName("space station");
        setPlanetType(SPACE_STATION);
    }

    public static SpaceStation create() {
        return new SpaceStation(PlanetId.newId());
    }

}
