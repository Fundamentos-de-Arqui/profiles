package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Phone {
    private final String phone;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{7,15}$");

    public Phone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
        
        String cleanPhone = phone.trim();
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new IllegalArgumentException("Invalid phone format: " + phone);
        }
        
        this.phone = cleanPhone;
    }

    public String value() {
        return phone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Phone that = (Phone) obj;
        return Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
    }

    @Override
    public String toString() {
        return phone;
    }
}