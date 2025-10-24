package com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfileWithRelations;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.PatientProfileWithRelationsResource;

public class PatientProfileWithRelationsResourceFromEntityAssembler {
    
    public static PatientProfileWithRelationsResource toResourceFromEntity(PatientProfileWithRelations entity) {
        var patient = entity.patient();
        var referredTherapist = patient.getReferredTherapist();
        
        return new PatientProfileWithRelationsResource(
                // Patient basic info
                patient.getId().value(),
                patient.getIdentity().firstNames().value(),
                patient.getIdentity().paternalSurname().value(),
                patient.getIdentity().maternalSurname().value(),
                patient.getIdentity().identityDocumentNumber().value(),
                patient.getIdentity().documentType().value(),
                patient.getIdentity().phone().value(),
                patient.getIdentity().email().value(),
                patient.getBirthData().birthPlace(),
                patient.getBirthData().birthDate().toString(),
                patient.getAge().firstAppointment(),
                patient.getAge().current(),
                patient.getGender().value(),
                patient.getMaritalStatus().value(),
                patient.getAddress().currentAddress(),
                patient.getAddress().district(),
                patient.getAddress().province(),
                patient.getAddress().region(),
                patient.getAddress().country(),
                patient.getReligion().value(),
                patient.getEducationData().educationLevel(),
                patient.getEducationData().occupation(),
                patient.getEducationData().currentEducationalInstitution(),
                referredTherapist != null ? referredTherapist.therapistName() : null,
                
                // Related entities
                entity.legalResponsible() != null ? 
                    LegalResponsibleProfileResourceFromEntityAssembler.toResourceFromEntity(entity.legalResponsible()) : null,
                entity.therapist() != null ? 
                    TherapistProfileResourceFromEntityAssembler.toResourceFromEntity(entity.therapist()) : null
        );
    }
}