package com.neotee.exploration_drone_controller.planet.domain;

import com.neotee.exploration_drone_controller.domainprimitives.Uranium;


import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.UUID;

import static com.neotee.exploration_drone_controller.planet.domain.PlanetType.SPACE_STATION;
import static com.neotee.exploration_drone_controller.planet.domain.PlanetVisitStatus.VISITED;


@Entity
@Getter
@Setter
public class SpaceStation extends Planet {


    public SpaceStation() {
        this.setId(UUID.randomUUID());
        this.planetType = SPACE_STATION;
        this.uranium = Uranium.fromAmount(0);
    }

}
