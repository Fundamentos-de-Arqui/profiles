package com.soulware.therapysystem.profiles.profiles.application.acl;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import java.util.Optional;

/**
 * Anti-Corruption Layer interface for Profiles Context.
 * This facade exposes selected operations to other bounded contexts.
 */
public interface ProfilesContextFacade {

    /**
     * Fetch a patient profile by its ID.
     * Used by other bounded contexts that need patient information.
     */
    Optional<PatientProfile> fetchPatientProfileById(Integer patientId);

    /**
     * Fetch a patient profile by document type and number.
     * Used by other bounded contexts for patient verification.
     */
    Optional<PatientProfile> fetchPatientProfileByDocument(String documentType, String documentNumber);

    /**
     * Verify if a patient profile exists.
     * Lightweight operation for existence checks.
     */
    boolean patientProfileExists(Integer patientId);
}