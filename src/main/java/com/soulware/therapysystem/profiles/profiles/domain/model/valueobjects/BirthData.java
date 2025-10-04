package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.time.LocalDate;
import java.util.Objects;

public final class BirthData {
    private final String birthPlace;
    private final LocalDate birthDate;

    public BirthData(String birthPlace, LocalDate birthDate) {
        if (birthPlace == null || birthPlace.trim().isEmpty()) {
            throw new IllegalArgumentException("Birth place cannot be null or empty");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        
        this.birthPlace = birthPlace.trim();
        this.birthDate = birthDate;
    }

    public String birthPlace() {
        return birthPlace;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BirthData birthData = (BirthData) obj;
        return Objects.equals(birthPlace, birthData.birthPlace) &&
               Objects.equals(birthDate, birthData.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthPlace, birthDate);
    }

    @Override
    public String toString() {
        return "BirthData{" +
                "birthPlace='" + birthPlace + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}