package com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto;

import java.time.LocalDateTime;

/**
 * DTO representing a complete therapist processing message from external systems
 * Uses the simple format that matches the actual POST structure
 */
public record TherapistProcessingMessage(
    String messageId,
    String messageType,
    LocalDateTime timestamp,
    String source,
    SimpleTherapistData therapistData
) {
}