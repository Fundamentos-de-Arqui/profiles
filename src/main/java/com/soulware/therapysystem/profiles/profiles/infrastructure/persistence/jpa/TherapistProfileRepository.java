package com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.TherapistProfileId;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TherapistProfile aggregate.
 * This will be implemented by a JPA repository implementation.
 */
public interface TherapistProfileRepository {

    TherapistProfile save(TherapistProfile therapistProfile);

    Optional<TherapistProfile> findById(TherapistProfileId id);

    List<TherapistProfile> findAll();

    void delete(TherapistProfile therapistProfile);

    void deleteById(TherapistProfileId id);

    Optional<TherapistProfile> findByDocumentTypeAndNumber(String documentType, String documentNumber);

    List<TherapistProfile> findBySpecialtyName(String specialtyName);
}