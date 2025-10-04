package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

public enum Religion {
    JUDAISM("Judaísmo"),
    CHRISTIANITY("Cristianismo"),
    ISLAM("Islam"),
    BUDDHISM("Budismo"),
    OTHER("Otra");

    private final String description;

    Religion(String description) {
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