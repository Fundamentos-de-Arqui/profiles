package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;

/**
 * Value object to hold a patient profile with its related entities.
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