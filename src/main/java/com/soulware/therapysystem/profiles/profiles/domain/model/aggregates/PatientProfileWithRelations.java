package com.soulware.therapysystem.profiles.profiles.domain.model.aggregates;

/**
 * Data transfer object to hold a patient profile with its related entities.
 */
public record PatientProfileWithRelations(
    PatientProfile patient,
    LegalResponsibleProfile legalResponsible,
    TherapistProfile therapist
) {
    
    public PatientProfileWithRelations {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        // legalResponsible and therapist can be null (optional relationships)
    }
}