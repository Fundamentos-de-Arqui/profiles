package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class Relationship {
    private final String description;

    public Relationship(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Relationship description cannot be null or empty");
        }
        this.description = description.trim();
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Relationship that = (Relationship) obj;
        return Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    @Override
    public String toString() {
        return description;
    }
}