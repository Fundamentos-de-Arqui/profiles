package com.soulware.therapysystem.profiles.profiles.domain.model.commands;

/**
 * Command to assign a therapist to a patient profile.
 */
public record AssignTherapistCommand(
    Integer patientProfileId,
    Integer therapistId
) {

    public AssignTherapistCommand {
        if (patientProfileId == null || patientProfileId <= 0)
            throw new IllegalArgumentException("Patient profile ID cannot be null or negative");
        if (therapistId == null || therapistId <= 0)
            throw new IllegalArgumentException("Therapist ID cannot be null or negative");
    }
}