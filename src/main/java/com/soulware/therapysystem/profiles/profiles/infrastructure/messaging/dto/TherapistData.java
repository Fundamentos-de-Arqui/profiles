package com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto;

/**
 * DTO representing therapist data from CustomerService
 */
public record TherapistData(
    String firstNames,
    String paternalSurname,
    String maternalSurname,
    String documentType,
    String documentNumber,
    String phone,
    String email,
    String nationality,
    String personalDataType,
    String university,
    String degree,
    String cmpNumber,
    String specialties
) {
}