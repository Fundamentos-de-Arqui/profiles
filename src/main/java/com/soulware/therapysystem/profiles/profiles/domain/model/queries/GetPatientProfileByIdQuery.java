package com.soulware.therapysystem.profiles.profiles.domain.model.queries;

public record GetPatientProfileByIdQuery(Integer patientProfileId) {
    
    public GetPatientProfileByIdQuery {
        if (patientProfileId == null || patientProfileId <= 0)
            throw new IllegalArgumentException("Patient profile ID must be positive");
    }
}