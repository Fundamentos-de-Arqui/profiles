package com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.LegalResponsibleProfileResource;

public class LegalResponsibleProfileResourceFromEntityAssembler {
    
    public static LegalResponsibleProfileResource toResourceFromEntity(LegalResponsibleProfile entity) {
        return new LegalResponsibleProfileResource(
                entity.getId().value(),
                entity.getIdentity().firstNames().value(),
                entity.getIdentity().paternalSurname().value(),
                entity.getIdentity().maternalSurname().value(),
                entity.getIdentity().identityDocumentNumber().value(),
                entity.getIdentity().documentType().value(),
                entity.getIdentity().phone().value(),
                entity.getIdentity().email().value(),
                entity.getRelationship().description()
        );
    }
}