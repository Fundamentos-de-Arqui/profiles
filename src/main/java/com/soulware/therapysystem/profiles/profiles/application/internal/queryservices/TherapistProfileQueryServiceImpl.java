package com.soulware.therapysystem.profiles.profiles.application.internal.queryservices;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.*;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.TherapistProfileId;
import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.TherapistProfileRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TherapistProfileQueryServiceImpl implements TherapistProfileQueryService {

    private final TherapistProfileRepository therapistProfileRepository;

    @Inject
    public TherapistProfileQueryServiceImpl(TherapistProfileRepository therapistProfileRepository) {
        this.therapistProfileRepository = therapistProfileRepository;
    }

    @Override
    public List<TherapistProfile> handle(GetAllTherapistProfilesQuery query) {
        return therapistProfileRepository.findAll();
    }

    @Override
    public Optional<TherapistProfile> handle(GetTherapistProfileByIdQuery query) {
        TherapistProfileId id = new TherapistProfileId(query.therapistProfileId());
        return therapistProfileRepository.findById(id);
    }

    @Override
    public List<TherapistProfile> handle(GetTherapistProfilesBySpecialtyQuery query) {
        return therapistProfileRepository.findBySpecialtyName(query.specialtyName());
    }
}