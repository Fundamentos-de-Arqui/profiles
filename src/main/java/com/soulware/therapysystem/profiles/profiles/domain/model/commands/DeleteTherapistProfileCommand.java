package com.soulware.therapysystem.profiles.profiles.domain.model.commands;

/**
 * @summary
 * Command to delete a TherapistProfile entity.
 */
public record DeleteTherapistProfileCommand(Integer therapistProfileId) {

    public DeleteTherapistProfileCommand {
        if (therapistProfileId == null || therapistProfileId <= 0)
            throw new IllegalArgumentException("Therapist profile ID must be positive");
    }
}