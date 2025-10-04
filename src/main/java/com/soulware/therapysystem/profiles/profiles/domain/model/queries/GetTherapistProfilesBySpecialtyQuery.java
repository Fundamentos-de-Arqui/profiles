package com.soulware.therapysystem.profiles.profiles.domain.model.queries;

public record GetTherapistProfilesBySpecialtyQuery(String specialtyName) {
    
    public GetTherapistProfilesBySpecialtyQuery {
        if (specialtyName == null || specialtyName.isBlank())
            throw new IllegalArgumentException("Specialty name cannot be null or empty");
    }
}