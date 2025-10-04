package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class FirstNames {
    private final String firstNames;

    public FirstNames(String firstNames) {
        if (firstNames == null || firstNames.trim().isEmpty()) {
            throw new IllegalArgumentException("First names cannot be null or empty");
        }
        this.firstNames = firstNames.trim();
    }

    public String value() {
        return firstNames;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FirstNames that = (FirstNames) obj;
        return Objects.equals(firstNames, that.firstNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstNames);
    }

    @Override
    public String toString() {
        return firstNames;
    }
}