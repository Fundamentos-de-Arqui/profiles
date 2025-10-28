package com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform;

import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreatePatientProfileCommand;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.CreatePatientProfileResource;

public class CreatePatientProfileCommandFromResourceAssembler {
    
    public static CreatePatientProfileCommand toCommandFromResource(CreatePatientProfileResource resource) {
        return new CreatePatientProfileCommand(
                resource.firstNames(),
                resource.paternalSurname(),
                resource.maternalSurname(),
                resource.identityDocumentNumber(),
                resource.documentType(),
                resource.phone(),
                resource.email(),
                resource.birthPlace(),
                resource.birthDate(),
                resource.firstAppointmentAge(),
                resource.currentAge(),
                resource.gender(),
                resource.maritalStatus(),
                resource.currentAddress(),
                resource.district(),
                resource.province(),
                resource.region(),
                resource.country(),
                resource.religion(),
                resource.educationLevel(),
                resource.occupation(),
                resource.currentEducationalInstitution(),
                resource.referredTherapistName(),
                resource.legalResponsibleId(),
                resource.therapistId()
        );
    }
}