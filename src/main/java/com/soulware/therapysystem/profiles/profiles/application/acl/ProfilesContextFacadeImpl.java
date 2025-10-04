package com.soulware.therapysystem.profiles.profiles.application.acl;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.PatientProfileRepository;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.PatientProfileId;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {

    private final PatientProfileRepository patientProfileRepository;

    @Inject
    public ProfilesContextFacadeImpl(PatientProfileRepository patientProfileRepository) {
        this.patientProfileRepository = patientProfileRepository;
    }

    @Override
    public Optional<PatientProfile> fetchPatientProfileById(Integer patientId) {
        try {
            PatientProfileId id = new PatientProfileId(patientId);
            return patientProfileRepository.findById(id);
        } catch (Exception e) {
            // Log error and return empty optional
            return Optional.empty();
        }
    }

    @Override
    public Optional<PatientProfile> fetchPatientProfileByDocument(String documentType, String documentNumber) {
        try {
            return patientProfileRepository.findByDocumentTypeAndNumber(documentType, documentNumber);
        } catch (Exception e) {
            // Log error and return empty optional
            return Optional.empty();
        }
    }

    @Override
    public boolean patientProfileExists(Integer patientId) {
        return fetchPatientProfileById(patientId).isPresent();
    }
}