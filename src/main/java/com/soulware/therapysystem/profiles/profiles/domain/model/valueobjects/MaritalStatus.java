package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

public enum MaritalStatus {
    SINGLE("Soltero/a"),
    MARRIED("Casado/a"),
    DIVORCED("Divorciado/a"),
    WIDOWED("Viudo/a"),
    OTHER("Otro");

    private final String description;

    MaritalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name() + ": " + description;
    }
}