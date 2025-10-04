package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class Specialty {
    private final String name;

    public Specialty(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialty name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Specialty specialty = (Specialty) obj;
        return Objects.equals(name, specialty.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}