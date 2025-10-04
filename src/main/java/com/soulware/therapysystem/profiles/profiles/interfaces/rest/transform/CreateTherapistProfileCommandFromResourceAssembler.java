package com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform;

import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateTherapistProfileCommand;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.CreateTherapistProfileResource;

public class CreateTherapistProfileCommandFromResourceAssembler {
    
    public static CreateTherapistProfileCommand toCommandFromResource(CreateTherapistProfileResource resource) {
        return new CreateTherapistProfileCommand(
                resource.firstNames(),
                resource.paternalSurname(),
                resource.maternalSurname(),
                resource.identityDocumentNumber(),
                resource.documentType(),
                resource.phone(),
                resource.email(),
                resource.specialtyName(),
                resource.attentionPlaceAddress()
        );
    }
}