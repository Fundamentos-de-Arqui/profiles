package com.soulware.therapysystem.profiles.profiles.domain.services;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.*;
import java.util.List;
import java.util.Optional;

/**
 * @summary
 * Interface for query operations on the LegalResponsibleProfile aggregate.
 * Defines contracts for retrieving legal responsible profile data.
 */
public interface LegalResponsibleProfileQueryService {

    /**
     * Handle retrieving all legal responsible profiles.
     * @param query The query with no parameters.
     * @return List of legal responsible profiles.
     */
    List<LegalResponsibleProfile> handle(GetAllLegalResponsibleProfilesQuery query);

    /**
     * Handle retrieving a legal responsible profile by its ID.
     * @param query Query containing the legal responsible profile ID.
     * @return Optional legal responsible profile.
     */
    Optional<LegalResponsibleProfile> handle(GetLegalResponsibleProfileByIdQuery query);

    /**
     * Handle retrieving a legal responsible profile by document type and number.
     * @param query Query containing the document type and number.
     * @return Optional legal responsible profile.
     */
    Optional<LegalResponsibleProfile> handle(GetLegalResponsibleProfileByDocumentQuery query);
}