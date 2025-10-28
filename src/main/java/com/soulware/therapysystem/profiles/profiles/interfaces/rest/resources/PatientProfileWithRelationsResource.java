package com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources;

public record PatientProfileWithRelationsResource(
        // Patient basic info
        Integer id,
        String firstNames,
        String paternalSurname,
        String maternalSurname,
        String identityDocumentNumber,
        String documentType,
        String phone,
        String email,
        String birthPlace,
        String birthDate,
        Integer firstAppointmentAge,
        Integer currentAge,
        String gender,
        String maritalStatus,
        String currentAddress,
        String district,
        String province,
        String region,
        String country,
        String religion,
        String educationLevel,
        String occupation,
        String currentEducationalInstitution,
        String referredTherapistName,
        
        // Related entities full info
        LegalResponsibleProfileResource legalResponsible,
        TherapistProfileResource therapist
) {}