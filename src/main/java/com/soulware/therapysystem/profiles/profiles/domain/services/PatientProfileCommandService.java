package com.soulware.therapysystem.profiles.profiles.domain.services;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.*;
import java.util.Optional;

/**
 * @summary
 * Interface for command operations on the PatientProfile aggregate.
 * Defines contracts for creating and deleting patient profile data.
 */
public interface PatientProfileCommandService {

    /**
     * Handle creating a new patient profile.
     * @param command The command containing patient profile data.
     * @return Optional patient profile if created successfully.
     */
    Optional<PatientProfile> handle(CreatePatientProfileCommand command);

    /**
     * Handle deleting a patient profile.
     * @param command The command containing the profile ID to delete.
     * @return True if deleted successfully, false otherwise.
     */
    boolean handle(DeletePatientProfileCommand command);

    /**
     * Handle assigning a legal responsible to a patient profile.
     * @param command The command containing patient and legal responsible IDs.
     * @return Optional patient profile if updated successfully.
     */
    Optional<PatientProfile> handle(AssignLegalResponsibleCommand command);

    /**
     * Handle assigning a therapist to a patient profile.
     * @param command The command containing patient and therapist IDs.
     * @return Optional patient profile if updated successfully.
     */
    Optional<PatientProfile> handle(AssignTherapistCommand command);
}