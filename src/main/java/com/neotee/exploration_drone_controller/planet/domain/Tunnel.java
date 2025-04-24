package com.neotee.exploration_drone_controller.planet.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
public abstract class Tunnel {

    @Id
    protected UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    protected Planet entryPlanet;

    @OneToOne
    protected Planet exitPlanet;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tunnel tunnel = (Tunnel) o;
        return Objects.equals(getId(), tunnel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public Tunnel() {
        this.id = UUID.randomUUID();
    }

    public abstract Boolean isInActive() ;

    public abstract Planet getExitPlanet() ;

    public abstract void shutdown();
}
