package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class MaternalSurname {
    private final String maternalSurname;

    public MaternalSurname(String maternalSurname) {
        // El apellido materno puede ser opcional en algunos casos
        this.maternalSurname = maternalSurname != null ? maternalSurname.trim() : "";
    }

    public String value() {
        return maternalSurname;
    }

    public boolean isEmpty() {
        return maternalSurname == null || maternalSurname.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MaternalSurname that = (MaternalSurname) obj;
        return Objects.equals(maternalSurname, that.maternalSurname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maternalSurname);
    }

    @Override
    public String toString() {
        return maternalSurname;
    }
}