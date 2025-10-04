package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class EducationData {
    private final String educationLevel;
    private final String occupation;
    private final String currentEducationalInstitution;

    public EducationData(String educationLevel, String occupation, String currentEducationalInstitution) {
        if (educationLevel == null || educationLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Education level cannot be null or empty");
        }
        if (occupation == null || occupation.trim().isEmpty()) {
            throw new IllegalArgumentException("Occupation cannot be null or empty");
        }
        // La institución educativa actual puede ser opcional
        
        this.educationLevel = educationLevel.trim();
        this.occupation = occupation.trim();
        this.currentEducationalInstitution = currentEducationalInstitution != null ? 
            currentEducationalInstitution.trim() : "";
    }

    public String educationLevel() {
        return educationLevel;
    }

    public String occupation() {
        return occupation;
    }

    public String currentEducationalInstitution() {
        return currentEducationalInstitution;
    }

    public boolean hasCurrentEducationalInstitution() {
        return currentEducationalInstitution != null && !currentEducationalInstitution.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EducationData that = (EducationData) obj;
        return Objects.equals(educationLevel, that.educationLevel) &&
               Objects.equals(occupation, that.occupation) &&
               Objects.equals(currentEducationalInstitution, that.currentEducationalInstitution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(educationLevel, occupation, currentEducationalInstitution);
    }

    @Override
    public String toString() {
        return "EducationData{" +
                "educationLevel='" + educationLevel + '\'' +
                ", occupation='" + occupation + '\'' +
                ", currentEducationalInstitution='" + currentEducationalInstitution + '\'' +
                '}';
    }
}