package com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto;

/**
 * DTO representing therapist data in the actual format used by the system
 * This matches the exact structure of the therapist POST endpoint
 */
public record SimpleTherapistData(
    String firstNames,
    String paternalSurname,
    String maternalSurname,
    String identityDocumentNumber,
    String documentType,
    String phone,
    String email,
    String specialtyName,
    String attentionPlaceAddress
) {
}