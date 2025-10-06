package com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.TherapistProfileResource;

public class TherapistProfileResourceFromEntityAssembler {
    
    public static TherapistProfileResource toResourceFromEntity(TherapistProfile entity) {
        return new TherapistProfileResource(
                entity.getId().value(),
                entity.getIdentity().firstNames().value(),
                entity.getIdentity().paternalSurname().value(),
                entity.getIdentity().maternalSurname().value(),
                entity.getIdentity().identityDocumentNumber().value(),
                entity.getIdentity().documentType().value(),
                entity.getIdentity().phone().value(),
                entity.getIdentity().email().value(),
                entity.getSpecialty().name(),
                entity.getAttentionPlace().address()
        );
    }
}