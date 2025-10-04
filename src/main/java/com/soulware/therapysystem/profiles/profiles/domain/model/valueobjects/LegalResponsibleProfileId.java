package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class LegalResponsibleProfileId {
    private final int id;

    public LegalResponsibleProfileId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Legal responsible profile ID must be positive");
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
        LegalResponsibleProfileId that = (LegalResponsibleProfileId) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LegalResponsibleProfileId{" + id + '}';
    }
}