package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class Age {
    private final int firstAppointment;
    private final int current;

    public Age(int firstAppointment, int current) {
        if (firstAppointment < 0) {
            throw new IllegalArgumentException("Age at first appointment cannot be negative");
        }
        if (current < 0) {
            throw new IllegalArgumentException("Current age cannot be negative");
        }
        if (current < firstAppointment) {
            throw new IllegalArgumentException("Current age cannot be less than age at first appointment");
        }
        
        this.firstAppointment = firstAppointment;
        this.current = current;
    }

    public int firstAppointment() {
        return firstAppointment;
    }

    public int current() {
        return current;
    }

    public int yearsSinceFirstAppointment() {
        return current - firstAppointment;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Age age = (Age) obj;
        return firstAppointment == age.firstAppointment && current == age.current;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstAppointment, current);
    }

    @Override
    public String toString() {
        return "Age{" +
                "firstAppointment=" + firstAppointment +
                ", current=" + current +
                '}';
    }
}