package com.neotee.exploration_drone_controller.domainprimitives;

import certification.ExplorationDroneControlException;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Load {

    private Integer capacity;

    @Embedded
    private Uranium uranium = Uranium.fromAmount(0);

    private Load(Integer capacity) {
        this.capacity = capacity;
    }

    private Load(Integer capacity, Uranium uranium) {
        this.capacity = capacity;
        this.uranium = uranium;
    }

    public static Load fromCapacity(Integer capacity) {
        if (capacity == null || capacity < 0)
            throw new ExplorationDroneControlException("Capacity must be non-null and non-negative"); 
        return new Load(capacity);
    }

    public static Load fromCapacityAndFilling(Integer capacity, Uranium uranium) {
        if (uranium == null)
            throw new ExplorationDroneControlException("Uranium must not be null"); 
        if (capacity == null)
            throw new ExplorationDroneControlException("Capacity must not be null"); 
        if (capacity < 0)
            throw new ExplorationDroneControlException("Capacity must not be negative"); 
        if (capacity < uranium.getAmount())
            throw new ExplorationDroneControlException("Uranium amount exceeds capacity"); 
        return new Load(capacity, uranium);
    }

    public Uranium availableCapacity() {
        return Uranium.fromAmount(capacity - uranium.getAmount());
    }

    public Load fillFrom(Uranium resource) {
        if (resource == null || resource.getAmount() <= 0)
            throw new ExplorationDroneControlException("Uranium to fill must be non-null and positive");

        int newAmount = uranium.getAmount() + resource.getAmount();

        if (newAmount > capacity) {
            newAmount = capacity;
        }
        return Load.fromCapacityAndFilling(capacity, Uranium.fromAmount(newAmount));
    }

    public Uranium leaveBehindWhenFillingFrom(Uranium resource) {
        if (resource == null || resource.getAmount() <=0)
            throw new ExplorationDroneControlException("Uranium to fill must be non-null and positive");

        int availableCapacity = availableCapacity().getAmount();
        int leftover = resource.getAmount() - availableCapacity;
        return Uranium.fromAmount(Math.max(leftover, 0));
    }

    public boolean hasMoreFreeCapacityThan(Load anotherLoad) {
        if (anotherLoad == null)
            throw new ExplorationDroneControlException("Compared load must not be null"); 
        return this.capacity - anotherLoad.capacity > 0;
    }
}
