package com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources;

public record CreatePatientProfileResource(
        String firstNames,
        String paternalSurname,
        String maternalSurname,
        String identityDocumentNumber,
        String documentType,
        String phone,
        String email,
        String birthPlace,
        String birthDate,
        Integer firstAppointmentAge,
        Integer currentAge,
        String gender,
        String maritalStatus,
        String currentAddress,
        String district,
        String province,
        String region,
        String country,
        String religion,
        String educationLevel,
        String occupation,
        String currentEducationalInstitution
) {
    public CreatePatientProfileResource {
        if (firstNames == null || firstNames.isBlank())
            throw new IllegalArgumentException("First names cannot be null or empty");
        if (paternalSurname == null || paternalSurname.isBlank())
            throw new IllegalArgumentException("Paternal surname cannot be null or empty");
        if (identityDocumentNumber == null || identityDocumentNumber.isBlank())
            throw new IllegalArgumentException("Identity document number cannot be null or empty");
        if (documentType == null || documentType.isBlank())
            throw new IllegalArgumentException("Document type cannot be null or empty");
        if (phone == null || phone.isBlank())
            throw new IllegalArgumentException("Phone cannot be null or empty");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email cannot be null or empty");
        if (birthPlace == null || birthPlace.isBlank())
            throw new IllegalArgumentException("Birth place cannot be null or empty");
        if (birthDate == null || birthDate.isBlank())
            throw new IllegalArgumentException("Birth date cannot be null or empty");
        if (firstAppointmentAge == null || firstAppointmentAge < 0)
            throw new IllegalArgumentException("First appointment age cannot be null or negative");
        if (currentAge == null || currentAge < 0)
            throw new IllegalArgumentException("Current age cannot be null or negative");
        if (gender == null || gender.isBlank())
            throw new IllegalArgumentException("Gender cannot be null or empty");
        if (maritalStatus == null || maritalStatus.isBlank())
            throw new IllegalArgumentException("Marital status cannot be null or empty");
        if (currentAddress == null || currentAddress.isBlank())
            throw new IllegalArgumentException("Current address cannot be null or empty");
        if (district == null || district.isBlank())
            throw new IllegalArgumentException("District cannot be null or empty");
        if (province == null || province.isBlank())
            throw new IllegalArgumentException("Province cannot be null or empty");
        if (region == null || region.isBlank())
            throw new IllegalArgumentException("Region cannot be null or empty");
        if (country == null || country.isBlank())
            throw new IllegalArgumentException("Country cannot be null or empty");
        if (religion == null || religion.isBlank())
            throw new IllegalArgumentException("Religion cannot be null or empty");
        if (educationLevel == null || educationLevel.isBlank())
            throw new IllegalArgumentException("Education level cannot be null or empty");
        if (occupation == null || occupation.isBlank())
            throw new IllegalArgumentException("Occupation cannot be null or empty");
    }
}