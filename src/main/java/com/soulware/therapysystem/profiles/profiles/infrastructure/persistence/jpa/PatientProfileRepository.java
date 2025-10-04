package com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.PatientProfileId;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PatientProfile aggregate.
 * This will be implemented by a JPA repository implementation.
 */
public interface PatientProfileRepository {

    PatientProfile save(PatientProfile patientProfile);

    Optional<PatientProfile> findById(PatientProfileId id);

    List<PatientProfile> findAll();

    void delete(PatientProfile patientProfile);

    void deleteById(PatientProfileId id);

    Optional<PatientProfile> findByDocumentTypeAndNumber(String documentType, String documentNumber);
}