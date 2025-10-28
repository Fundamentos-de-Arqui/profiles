package com.soulware.therapysystem.profiles.profiles.domain.model.commands;

/**
 * Command to assign a legal responsible to a patient profile.
 */
public record AssignLegalResponsibleCommand(
    Integer patientProfileId,
    Integer legalResponsibleId
) {

    public AssignLegalResponsibleCommand {
        if (patientProfileId == null || patientProfileId <= 0)
            throw new IllegalArgumentException("Patient profile ID cannot be null or negative");
        if (legalResponsibleId == null || legalResponsibleId <= 0)
            throw new IllegalArgumentException("Legal responsible ID cannot be null or negative");
    }
}