package com.neotee.exploration_drone_controller.core;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class AbstractEntity<ID> {

    @Id
    protected ID id;

    protected AbstractEntity() {
    }

    protected AbstractEntity(ID id) {
        this.id = id;
    }
}