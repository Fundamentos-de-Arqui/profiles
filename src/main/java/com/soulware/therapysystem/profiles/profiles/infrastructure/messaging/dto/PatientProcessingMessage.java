package com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto;

import java.time.LocalDateTime;

/**
 * DTO representing a patient processing message received from CustomerService
 */
public record PatientProcessingMessage(
    String messageId,
    String fileName,
    LocalDateTime uploadedAt,
    CompletePatientDataRequest patientData,
    int retryCount,
    String status
) {
}