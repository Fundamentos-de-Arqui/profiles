package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class Gender {
    private final String value;

    public Gender(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender value cannot be null or empty");
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
        Gender gender = (Gender) obj;
        return Objects.equals(value, gender.value);
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