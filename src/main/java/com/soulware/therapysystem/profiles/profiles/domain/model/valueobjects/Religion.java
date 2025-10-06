package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class Religion {
    private final String value;

    public Religion(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Religion value cannot be null or empty");
        }
        this.value = value.trim();
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Religion religion = (Religion) obj;
        return Objects.equals(value, religion.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}