package com.soulware.therapysystem.profiles.profiles.application.internal.queryservices;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.*;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.LegalResponsibleProfileId;
import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.LegalResponsibleProfileRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LegalResponsibleProfileQueryServiceImpl implements LegalResponsibleProfileQueryService {

    private final LegalResponsibleProfileRepository legalResponsibleProfileRepository;

    @Inject
    public LegalResponsibleProfileQueryServiceImpl(LegalResponsibleProfileRepository legalResponsibleProfileRepository) {
        this.legalResponsibleProfileRepository = legalResponsibleProfileRepository;
    }

    @Override
    public List<LegalResponsibleProfile> handle(GetAllLegalResponsibleProfilesQuery query) {
        return legalResponsibleProfileRepository.findAll();
    }

    @Override
    public Optional<LegalResponsibleProfile> handle(GetLegalResponsibleProfileByIdQuery query) {
        LegalResponsibleProfileId id = new LegalResponsibleProfileId(query.legalResponsibleProfileId());
        return legalResponsibleProfileRepository.findById(id);
    }
}