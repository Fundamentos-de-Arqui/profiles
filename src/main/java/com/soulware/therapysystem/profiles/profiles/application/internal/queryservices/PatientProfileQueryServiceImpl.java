package com.soulware.therapysystem.profiles.profiles.application.internal.queryservices;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.*;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.PatientProfileId;
import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.PatientProfileRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PatientProfileQueryServiceImpl implements PatientProfileQueryService {

    private final PatientProfileRepository patientProfileRepository;

    @Inject
    public PatientProfileQueryServiceImpl(PatientProfileRepository patientProfileRepository) {
        this.patientProfileRepository = patientProfileRepository;
    }

    @Override
    public List<PatientProfile> handle(GetAllPatientProfilesQuery query) {
        return patientProfileRepository.findAll();
    }

    @Override
    public Optional<PatientProfile> handle(GetPatientProfileByIdQuery query) {
        PatientProfileId id = new PatientProfileId(query.patientProfileId());
        return patientProfileRepository.findById(id);
    }

    @Override
    public Optional<PatientProfile> handle(GetPatientProfileByDocumentQuery query) {
        return patientProfileRepository.findByDocumentTypeAndNumber(
            query.documentType(),
            query.documentNumber()
        );
    }
}