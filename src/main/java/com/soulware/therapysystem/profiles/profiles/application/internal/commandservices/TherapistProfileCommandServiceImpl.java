package com.soulware.therapysystem.profiles.profiles.application.internal.commandservices;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateTherapistProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.DeleteTherapistProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.factories.ProfileFactory;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.*;
import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.model.repositories.TherapistProfileRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class TherapistProfileCommandServiceImpl implements TherapistProfileCommandService {

    private final TherapistProfileRepository therapistProfileRepository;

    @Inject
    public TherapistProfileCommandServiceImpl(TherapistProfileRepository therapistProfileRepository) {
        this.therapistProfileRepository = therapistProfileRepository;
    }

    @Override
    @Transactional
    public Optional<TherapistProfile> handle(CreateTherapistProfileCommand command) {
        // Create Identity using factory
        Identity identity = ProfileFactory.createIdentity(
            command.firstNames(),
            command.paternalSurname(),
            command.maternalSurname(),
            command.identityDocumentNumber(),
            new DocumentType(command.documentType()),
            command.phone(),
            command.email()
        );

        // Create Specialty and AttentionPlace
        Specialty specialty = ProfileFactory.createSpecialty(command.specialtyName());
        AttentionPlace attentionPlace = ProfileFactory.createAttentionPlace(command.attentionPlaceAddress());

        // Create TherapistProfile using factory (sin ID, será generado por la DB)
        TherapistProfile therapistProfile = ProfileFactory.createTherapistProfile(
            identity,
            specialty,
            attentionPlace
        );

        // Save using repository - let domain/infrastructure exceptions propagate
        TherapistProfile saved = therapistProfileRepository.save(therapistProfile);
        return Optional.of(saved);
    }

    @Override
    public boolean handle(DeleteTherapistProfileCommand command) {
        try {
            TherapistProfileId id = new TherapistProfileId(command.therapistProfileId());
            therapistProfileRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            // Log error
            return false;
        }
    }
}