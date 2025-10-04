package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

public enum DocumentType {
    DNI("Documento Nacional de Identidad"),
    RUC("Registro Único del Contribuyente"),
    PASSPORT("Pasaporte"),
    OTHER("Otro");

    private final String description;

    DocumentType(String description) {
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