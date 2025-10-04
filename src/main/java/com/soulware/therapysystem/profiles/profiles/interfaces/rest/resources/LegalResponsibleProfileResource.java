package com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources;

public record LegalResponsibleProfileResource(
        Integer id,
        String firstNames,
        String paternalSurname,
        String maternalSurname,
        String identityDocumentNumber,
        String documentType,
        String phone,
        String email,
        String relationship
) {}