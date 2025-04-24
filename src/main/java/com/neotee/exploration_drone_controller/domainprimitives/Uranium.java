package com.neotee.exploration_drone_controller.domainprimitives;

import certification.ExplorationDroneControlException;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class Uranium {

    private Integer amount = 0;

    private Uranium(Integer uraniumAmount) {
        this.amount = uraniumAmount;
    }

    public static Uranium fromAmount(Integer uraniumAmount) {
        if (uraniumAmount == null || uraniumAmount < 0)
            throw new ExplorationDroneControlException("Uranium amount must be non-null and non-negative");
        return new Uranium(uraniumAmount);
    }

    public Uranium addTo(Uranium additionalUranium) {
        if (additionalUranium == null)
            throw new ExplorationDroneControlException("Uranium to add must not be null");
        return Uranium.fromAmount(this.amount + additionalUranium.amount);
    }

    public Uranium subtractFrom(Uranium otherUranium) {
        if (otherUranium == null)
            throw new ExplorationDroneControlException("Uranium to subtract from must not be null");
        if (amount < otherUranium.amount) return Uranium.fromAmount(otherUranium.amount - this.amount);
        return Uranium.fromAmount(0);
    }


    public boolean isGreaterThan(Uranium otherUranium) {
        if (otherUranium == null)
            throw new ExplorationDroneControlException("Uranium to compare must not be null");
        return this.amount > otherUranium.amount;
    }

    public boolean isGreaterEqualThan(Uranium otherUranium) {
        if (otherUranium == null)
            throw new ExplorationDroneControlException("Uranium to compare must not be null");
        return this.amount >= otherUranium.amount;
    }

    public boolean isZero() {
        return amount == 0;
    }

    public int getAmount() {
        return amount;
    }
}
