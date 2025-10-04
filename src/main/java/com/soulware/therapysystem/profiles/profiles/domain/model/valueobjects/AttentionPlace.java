package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class AttentionPlace {
    private final String address;

    public AttentionPlace(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Attention place address cannot be null or empty");
        }
        this.address = address.trim();
    }

    public String address() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AttentionPlace that = (AttentionPlace) obj;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        return address;
    }
}