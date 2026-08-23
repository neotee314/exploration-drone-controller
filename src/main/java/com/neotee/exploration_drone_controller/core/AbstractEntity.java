package com.neotee.exploration_drone_controller.core;

import lombok.Getter;

@Getter
public abstract class AbstractEntity<ID> {

    protected ID id;
    private AuditDateTime createdAt;
    private AuditDateTime updatedAt;
    protected void touch() {
        this.updatedAt = AuditDateTime.now();
    }
}