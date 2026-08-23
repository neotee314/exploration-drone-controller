package com.neotee.exploration_drone_controller.domainprimitives;

import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public abstract class GenericId implements Serializable {

    protected UUID id;

    protected GenericId(UUID id) {
        if (id == null) throw new DomainValidationException("id", "must not be null");
        this.id = id;
    }

    public String value() {
        return id.toString();
    }

    @Override
    public String toString() {
        return value();
    }
}