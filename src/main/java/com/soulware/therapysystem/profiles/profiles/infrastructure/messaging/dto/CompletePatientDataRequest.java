package com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO representing complete patient data received from CustomerService
 */
public record CompletePatientDataRequest(
    // Patient basic information
    String firstNames,
    String paternalSurname,
    String maternalSurname,
    String documentType,
    String documentNumber,
    String phone,
    String email,
    LocalDate birthDate,
    String birthPlace,
    Integer ageFirstAppointment,
    Integer ageCurrent,
    String gender,
    String maritalStatus,
    String religion,
    String educationLevel,
    String occupation,
    String currentEducationalInstitution,
    
    // Address information
    String currentAddress,
    String district,
    String province,
    String region,
    String country,
    
    // Medical information
    String medicalDiagnosis,
    String problemIdentified,
    String additionalNotes,
    
    // Billing information
    String receiptType,
    String businessName,
    String holder,
    String rucOrDni,
    String billingAddress,
    
    // Related profiles data
    List<LegalResponsibleData> legalResponsibles,
    List<TherapistData> therapists
) {
}