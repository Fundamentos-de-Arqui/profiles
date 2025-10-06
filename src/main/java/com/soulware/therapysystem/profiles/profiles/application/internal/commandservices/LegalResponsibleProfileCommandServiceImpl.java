package com.soulware.therapysystem.profiles.profiles.application.internal.commandservices;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateLegalResponsibleProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.DeleteLegalResponsibleProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.factories.ProfileFactory;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.*;
import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.model.repositories.LegalResponsibleProfileRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class LegalResponsibleProfileCommandServiceImpl implements LegalResponsibleProfileCommandService {

    private final LegalResponsibleProfileRepository legalResponsibleProfileRepository;

    @Inject
    public LegalResponsibleProfileCommandServiceImpl(LegalResponsibleProfileRepository legalResponsibleProfileRepository) {
        this.legalResponsibleProfileRepository = legalResponsibleProfileRepository;
    }

    @Override
    public Optional<LegalResponsibleProfile> handle(CreateLegalResponsibleProfileCommand command) {
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

        // Create Relationship
        Relationship relationship = ProfileFactory.createRelationship(command.relationship());

        // Create LegalResponsibleProfile using factory (sin ID, será generado por la DB)
        LegalResponsibleProfile legalResponsibleProfile = ProfileFactory.createLegalResponsibleProfile(
            identity,
            relationship
        );

        // Save using repository - let domain/infrastructure exceptions propagate
        LegalResponsibleProfile saved = legalResponsibleProfileRepository.save(legalResponsibleProfile);
        return Optional.of(saved);
    }

    @Override
    public boolean handle(DeleteLegalResponsibleProfileCommand command) {
        try {
            LegalResponsibleProfileId id = new LegalResponsibleProfileId(command.legalResponsibleProfileId());
            legalResponsibleProfileRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            // Log error
            System.err.println("Error deleting legal responsible profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}