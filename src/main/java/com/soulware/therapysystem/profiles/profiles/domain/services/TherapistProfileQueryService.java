package com.soulware.therapysystem.profiles.profiles.domain.services;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.*;
import java.util.List;
import java.util.Optional;

/**
 * @summary
 * Interface for query operations on the TherapistProfile aggregate.
 * Defines contracts for retrieving therapist profile data.
 */
public interface TherapistProfileQueryService {

    /**
     * Handle retrieving all therapist profiles.
     * @param query The query with no parameters.
     * @return List of therapist profiles.
     */
    List<TherapistProfile> handle(GetAllTherapistProfilesQuery query);

    /**
     * Handle retrieving a therapist profile by its ID.
     * @param query Query containing the therapist profile ID.
     * @return Optional therapist profile.
     */
    Optional<TherapistProfile> handle(GetTherapistProfileByIdQuery query);

    /**
     * Handle retrieving therapist profiles by specialty.
     * @param query Query containing the specialty name.
     * @return List of therapist profiles with the specified specialty.
     */
    List<TherapistProfile> handle(GetTherapistProfilesBySpecialtyQuery query);
}