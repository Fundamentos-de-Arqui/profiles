package com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform;

import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateLegalResponsibleProfileCommand;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.CreateLegalResponsibleProfileResource;

public class CreateLegalResponsibleProfileCommandFromResourceAssembler {
    
    public static CreateLegalResponsibleProfileCommand toCommandFromResource(CreateLegalResponsibleProfileResource resource) {
        return new CreateLegalResponsibleProfileCommand(
                resource.firstNames(),
                resource.paternalSurname(),
                resource.maternalSurname(),
                resource.identityDocumentNumber(),
                resource.documentType(),
                resource.phone(),
                resource.email(),
                resource.relationship()
        );
    }
}