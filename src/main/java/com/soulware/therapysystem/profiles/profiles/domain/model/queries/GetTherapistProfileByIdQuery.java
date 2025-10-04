package com.soulware.therapysystem.profiles.profiles.domain.model.queries;

public record GetTherapistProfileByIdQuery(Integer therapistProfileId) {
    
    public GetTherapistProfileByIdQuery {
        if (therapistProfileId == null || therapistProfileId <= 0)
            throw new IllegalArgumentException("Therapist profile ID must be positive");
    }
}