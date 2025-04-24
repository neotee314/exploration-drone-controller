package com.neotee.exploration_drone_controller.planet.domain;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import com.neotee.exploration_drone_controller.domainprimitives.CompassPointPath;
import com.neotee.exploration_drone_controller.domainprimitives.Load;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;
import com.neotee.exploration_drone_controller.explorationdrone.domain.TransportState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Drone {

    @Id
    protected UUID id;

    protected String name;

    @Embedded
    protected CompassPointPath commandHistory;

    @ManyToOne
    @JoinColumn(name = "planet_id")
    protected Planet planet;

    @Enumerated(EnumType.STRING)
    protected TransportState transportState;

    @Embedded
    protected Load load;


    public abstract void move(CompassPoint movement);

    public abstract void transport();

    public abstract void gohome();

    public abstract void explore();

    public abstract void mine();

    public abstract boolean isTransported();


    public abstract Uranium getUranium();


    public abstract void addToPlanet(Planet planet);

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Drone drone = (Drone) o;
        return Objects.equals(getId(), drone.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
