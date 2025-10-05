package com.soulware.therapysystem.profiles.profiles.domain.model.queries;

public record GetLegalResponsibleProfileByDocumentQuery(String documentType, String documentNumber) {
    
    public GetLegalResponsibleProfileByDocumentQuery {
        if (documentType == null || documentType.isBlank())
            throw new IllegalArgumentException("Document type cannot be null or empty");
        if (documentNumber == null || documentNumber.isBlank())
            throw new IllegalArgumentException("Document number cannot be null or empty");
    }
}