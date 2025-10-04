package com.soulware.therapysystem.profiles.profiles.domain.services;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.*;
import java.util.Optional;

/**
 * @summary
 * Interface for command operations on the LegalResponsibleProfile aggregate.
 * Defines contracts for creating and deleting legal responsible profile data.
 */
public interface LegalResponsibleProfileCommandService {

    /**
     * Handle creating a new legal responsible profile.
     * @param command The command containing legal responsible profile data.
     * @return Optional legal responsible profile if created successfully.
     */
    Optional<LegalResponsibleProfile> handle(CreateLegalResponsibleProfileCommand command);

    /**
     * Handle deleting a legal responsible profile.
     * @param command The command containing the profile ID to delete.
     * @return True if deleted successfully, false otherwise.
     */
    boolean handle(DeleteLegalResponsibleProfileCommand command);
}