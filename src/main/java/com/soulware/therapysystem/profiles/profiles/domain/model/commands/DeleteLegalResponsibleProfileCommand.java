package com.soulware.therapysystem.profiles.profiles.domain.model.commands;

/**
 * @summary
 * Command to delete a LegalResponsibleProfile entity.
 */
public record DeleteLegalResponsibleProfileCommand(Integer legalResponsibleProfileId) {

    public DeleteLegalResponsibleProfileCommand {
        if (legalResponsibleProfileId == null || legalResponsibleProfileId <= 0)
            throw new IllegalArgumentException("Legal responsible profile ID must be positive");
    }
}