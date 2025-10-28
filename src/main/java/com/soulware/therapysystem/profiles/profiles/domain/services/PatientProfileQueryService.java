package com.soulware.therapysystem.profiles.profiles.domain.services;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfileWithRelations;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.*;
import java.util.List;
import java.util.Optional;

/**
 * @summary
 * Interface for query operations on the PatientProfile aggregate.
 * Defines contracts for retrieving patient profile data.
 */
public interface PatientProfileQueryService {

    /**
     * Handle retrieving all patient profiles.
     * @param query The query with no parameters.
     * @return List of patient profiles.
     */
    List<PatientProfile> handle(GetAllPatientProfilesQuery query);

    /**
     * Handle retrieving a patient profile by its ID.
     * @param query Query containing the patient profile ID.
     * @return Optional patient profile.
     */
    Optional<PatientProfile> handle(GetPatientProfileByIdQuery query);

    /**
     * Handle retrieving a patient profile by document type and number.
     * @param query Query containing the document type and number.
     * @return Optional patient profile.
     */
    Optional<PatientProfile> handle(GetPatientProfileByDocumentQuery query);

    /**
     * Handle retrieving a patient profile with related entities by document.
     * @param query Query containing the document type and number.
     * @return Optional patient profile with relations.
     */
    Optional<PatientProfileWithRelations> handle(GetPatientProfileWithRelationsByDocumentQuery query);
}