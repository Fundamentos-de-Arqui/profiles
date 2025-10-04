package com.soulware.therapysystem.profiles.profiles.domain.model.queries;

public record GetPatientProfileByDocumentQuery(String documentType, String documentNumber) {
    
    public GetPatientProfileByDocumentQuery {
        if (documentType == null || documentType.isBlank())
            throw new IllegalArgumentException("Document type cannot be null or empty");
        if (documentNumber == null || documentNumber.isBlank())
            throw new IllegalArgumentException("Document number cannot be null or empty");
    }
}