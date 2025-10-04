package com.soulware.therapysystem.profiles.profiles.domain.model.commands;

/**
 * @summary
 * Command to delete a PatientProfile entity.
 */
public record DeletePatientProfileCommand(Integer patientProfileId) {

    public DeletePatientProfileCommand {
        if (patientProfileId == null || patientProfileId <= 0)
            throw new IllegalArgumentException("Patient profile ID must be positive");
    }
}