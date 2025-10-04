package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class TherapistProfileId {
    private final int id;

    public TherapistProfileId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Therapist profile ID must be positive");
        }
        this.id = id;
    }

    public int value() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TherapistProfileId that = (TherapistProfileId) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TherapistProfileId{" + id + '}';
    }
}