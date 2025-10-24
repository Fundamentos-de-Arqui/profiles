package com.soulware.therapysystem.profiles.profiles.domain.model.queries;

/**
 * Query to get a patient profile with related legal responsible and therapist by document.
 */
public record GetPatientProfileWithRelationsByDocumentQuery(
    String documentType,
    String documentNumber
) {

    public GetPatientProfileWithRelationsByDocumentQuery {
        if (documentType == null || documentType.isBlank())
            throw new IllegalArgumentException("Document type cannot be null or empty");
        if (documentNumber == null || documentNumber.isBlank())
            throw new IllegalArgumentException("Document number cannot be null or empty");
    }
}