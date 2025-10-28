package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

/**
 * Value object representing a referred therapist name for patient profiles.
 * Simple value object without validation since it's optional.
 */
public record ReferredTherapist(String therapistName) {
}