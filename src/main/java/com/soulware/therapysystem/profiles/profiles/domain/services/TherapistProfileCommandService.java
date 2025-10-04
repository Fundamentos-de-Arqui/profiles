package com.soulware.therapysystem.profiles.profiles.domain.services;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.*;
import java.util.Optional;

/**
 * @summary
 * Interface for command operations on the TherapistProfile aggregate.
 * Defines contracts for creating and deleting therapist profile data.
 */
public interface TherapistProfileCommandService {

    /**
     * Handle creating a new therapist profile.
     * @param command The command containing therapist profile data.
     * @return Optional therapist profile if created successfully.
     */
    Optional<TherapistProfile> handle(CreateTherapistProfileCommand command);

    /**
     * Handle deleting a therapist profile.
     * @param command The command containing the profile ID to delete.
     * @return True if deleted successfully, false otherwise.
     */
    boolean handle(DeleteTherapistProfileCommand command);
}