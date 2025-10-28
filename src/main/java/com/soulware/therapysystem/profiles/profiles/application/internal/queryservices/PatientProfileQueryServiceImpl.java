package com.soulware.therapysystem.profiles.profiles.application.internal.queryservices;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.PatientProfileWithRelations;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.*;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.PatientProfileId;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.LegalResponsibleProfileId;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.TherapistProfileId;
import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.model.repositories.PatientProfileRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PatientProfileQueryServiceImpl implements PatientProfileQueryService {

    private final PatientProfileRepository patientProfileRepository;
    private final LegalResponsibleProfileQueryService legalResponsibleQueryService;
    private final TherapistProfileQueryService therapistQueryService;

    @Inject
    public PatientProfileQueryServiceImpl(PatientProfileRepository patientProfileRepository,
                                         LegalResponsibleProfileQueryService legalResponsibleQueryService,
                                         TherapistProfileQueryService therapistQueryService) {
        this.patientProfileRepository = patientProfileRepository;
        this.legalResponsibleQueryService = legalResponsibleQueryService;
        this.therapistQueryService = therapistQueryService;
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

    @Override
    public Optional<PatientProfileWithRelations> handle(GetPatientProfileWithRelationsByDocumentQuery query) {
        // First, find the patient
        Optional<PatientProfile> patientOpt = patientProfileRepository.findByDocumentTypeAndNumber(
            query.documentType(),
            query.documentNumber()
        );

        if (patientOpt.isEmpty()) {
            return Optional.empty();
        }

        PatientProfile patient = patientOpt.get();
        
        // Find related legal responsible if ID exists
        LegalResponsibleProfile legalResponsible = null;
        if (patient.getLegalResponsibleId() != null) {
            GetLegalResponsibleProfileByIdQuery legalQuery = new GetLegalResponsibleProfileByIdQuery(patient.getLegalResponsibleId());
            Optional<LegalResponsibleProfile> legalOpt = legalResponsibleQueryService.handle(legalQuery);
            legalResponsible = legalOpt.orElse(null);
        }

        // Find related therapist if ID exists
        TherapistProfile therapist = null;
        if (patient.getTherapistId() != null) {
            GetTherapistProfileByIdQuery therapistQuery = new GetTherapistProfileByIdQuery(patient.getTherapistId());
            Optional<TherapistProfile> therapistOpt = therapistQueryService.handle(therapistQuery);
            therapist = therapistOpt.orElse(null);
        }

        // Create the combined result
        PatientProfileWithRelations result = new PatientProfileWithRelations(patient, legalResponsible, therapist);
        return Optional.of(result);
    }
}