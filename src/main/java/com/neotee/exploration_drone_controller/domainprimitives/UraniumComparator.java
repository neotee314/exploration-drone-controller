package com.neotee.exploration_drone_controller.domainprimitives;

import com.neotee.exploration_drone_controller.exceptions.DomainValidationException;

import java.util.Comparator;

public class UraniumComparator implements Comparator<Uranium> {
    @Override
    public int compare(Uranium u1, Uranium u2) {
        if (u1 == null || u2 == null) {
            throw new DomainValidationException("Uranium","Uranium objects must not be null");
        }
        return Integer.compare(u1.getAmount(), u2.getAmount());
    }
}