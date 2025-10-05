package com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto;

/**
 * DTO representing legal responsible data from CustomerService
 */
public record LegalResponsibleData(
    String firstNames,
    String paternalSurname,
    String maternalSurname,
    String documentType,
    String documentNumber,
    String phone,
    String email,
    String profession,
    String workAddress,
    String relationship,
    String emergencyContactFirstNames,
    String emergencyContactLastName,
    String emergencyContactPhone
) {
}