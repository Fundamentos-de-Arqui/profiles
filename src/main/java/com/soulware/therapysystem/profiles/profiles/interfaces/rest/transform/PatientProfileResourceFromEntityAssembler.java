package com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.PatientProfileResource;

public class PatientProfileResourceFromEntityAssembler {
    
    public static PatientProfileResource toResourceFromEntity(PatientProfile entity) {
        return new PatientProfileResource(
                entity.getId().value(),
                entity.getIdentity().firstNames().value(),
                entity.getIdentity().paternalSurname().value(),
                entity.getIdentity().maternalSurname().value(),
                entity.getIdentity().identityDocumentNumber().value(),
                entity.getIdentity().documentType().name(),
                entity.getIdentity().phone().value(),
                entity.getIdentity().email().value(),
                entity.getBirthData().birthPlace(),
                entity.getBirthData().birthDate().toString(),
                entity.getAge().firstAppointment(),
                entity.getAge().current(),
                entity.getGender().name(),
                entity.getMaritalStatus().name(),
                entity.getAddress().currentAddress(),
                entity.getAddress().district(),
                entity.getAddress().province(),
                entity.getAddress().region(),
                entity.getAddress().country(),
                entity.getReligion().name(),
                entity.getEducationData().educationLevel(),
                entity.getEducationData().occupation(),
                entity.getEducationData().currentEducationalInstitution()
        );
    }
}