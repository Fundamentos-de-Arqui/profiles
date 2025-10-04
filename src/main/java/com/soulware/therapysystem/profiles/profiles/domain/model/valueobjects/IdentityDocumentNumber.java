package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

public final class IdentityDocumentNumber {
    private final String identityDocumentNumber;
    private static final Pattern DNI_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern RUC_PATTERN = Pattern.compile("^\\d{11}$");

    public IdentityDocumentNumber(String identityDocumentNumber) {
        if (identityDocumentNumber == null || identityDocumentNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Identity document number cannot be null or empty");
        }
        this.identityDocumentNumber = identityDocumentNumber.trim();
    }

    public String value() {
        return identityDocumentNumber;
    }

    public boolean isValidDNI() {
        return DNI_PATTERN.matcher(identityDocumentNumber).matches();
    }

    public boolean isValidRUC() {
        return RUC_PATTERN.matcher(identityDocumentNumber).matches();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IdentityDocumentNumber that = (IdentityDocumentNumber) obj;
        return Objects.equals(identityDocumentNumber, that.identityDocumentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityDocumentNumber);
    }

    @Override
    public String toString() {
        return identityDocumentNumber;
    }
}