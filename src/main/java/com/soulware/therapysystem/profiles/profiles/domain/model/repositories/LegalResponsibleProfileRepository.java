package com.soulware.therapysystem.profiles.profiles.domain.model.repositories;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.LegalResponsibleProfileId;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LegalResponsibleProfile aggregate.
 * This will be implemented by a JPA repository implementation.
 */
public interface LegalResponsibleProfileRepository {

    LegalResponsibleProfile save(LegalResponsibleProfile legalResponsibleProfile);

    Optional<LegalResponsibleProfile> findById(LegalResponsibleProfileId id);

    List<LegalResponsibleProfile> findAll();

    void delete(LegalResponsibleProfile legalResponsibleProfile);

    void deleteById(LegalResponsibleProfileId id);

    Optional<LegalResponsibleProfile> findByDocumentTypeAndNumber(String documentType, String documentNumber);
}