package com.neotee.exploration_drone_controller.explorationdrone.domain;

import certification.ExplorationDroneControlException;
import com.neotee.exploration_drone_controller.domainprimitives.Uranium;

import java.util.Comparator;

public class UraniumComparator implements Comparator<Uranium> {
    @Override
    public int compare(Uranium u1, Uranium u2) {
        if (u1 == null || u2 == null) {
            throw new ExplorationDroneControlException("Uranium objects must not be null");
        }
        return Integer.compare(u1.getAmount(), u2.getAmount());
    }
}
