package com.soulware.therapysystem.profiles.profiles.domain.model.commands;

/**
 * @summary
 * Command to create a TherapistProfile entity.
 */
public record CreateTherapistProfileCommand(
    String firstNames,
    String paternalSurname,
    String maternalSurname,
    String identityDocumentNumber,
    String documentType,
    String phone,
    String email,
    String specialtyName,
    String attentionPlaceAddress
) {

    public CreateTherapistProfileCommand {
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
        if (specialtyName == null || specialtyName.isBlank())
            throw new IllegalArgumentException("Specialty name cannot be null or empty");
        if (attentionPlaceAddress == null || attentionPlaceAddress.isBlank())
            throw new IllegalArgumentException("Attention place address cannot be null or empty");
    }
}