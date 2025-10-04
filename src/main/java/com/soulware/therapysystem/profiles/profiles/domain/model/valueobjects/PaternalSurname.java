package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class PaternalSurname {
    private final String paternalSurname;

    public PaternalSurname(String paternalSurname) {
        if (paternalSurname == null || paternalSurname.trim().isEmpty()) {
            throw new IllegalArgumentException("Paternal surname cannot be null or empty");
        }
        this.paternalSurname = paternalSurname.trim();
    }

    public String value() {
        return paternalSurname;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PaternalSurname that = (PaternalSurname) obj;
        return Objects.equals(paternalSurname, that.paternalSurname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paternalSurname);
    }

    @Override
    public String toString() {
        return paternalSurname;
    }
}