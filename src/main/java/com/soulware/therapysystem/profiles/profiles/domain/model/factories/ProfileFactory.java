package com.soulware.therapysystem.profiles.profiles.domain.model.factories;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.*;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.*;

/**
 * Factory para crear instancias de los diferentes tipos de profiles.
 * Centraliza la lógica de creación y validación de aggregates.
 */
public class ProfileFactory {

    /**
     * Crea un nuevo PatientProfile sin ID (para que la base de datos lo genere).
     */
    public static PatientProfile createPatientProfile(Identity identity, 
                                                     BirthData birthData, 
                                                     Age age, 
                                                     Gender gender, 
                                                     MaritalStatus maritalStatus, 
                                                     Address address, 
                                                     Religion religion, 
                                                     EducationData educationData,
                                                     ReferredTherapist referredTherapist,
                                                     Integer legalResponsibleId,
                                                     Integer therapistId) {
        return new PatientProfile(
            identity,
            birthData,
            age,
            gender,
            maritalStatus,
            address,
            religion,
            educationData,
            referredTherapist,
            legalResponsibleId,
            therapistId
        );
    }

    /**
     * Crea un nuevo PatientProfile sin ReferredTherapist ni relaciones (backward compatibility).
     */
    public static PatientProfile createPatientProfile(Identity identity, 
                                                     BirthData birthData, 
                                                     Age age, 
                                                     Gender gender, 
                                                     MaritalStatus maritalStatus, 
                                                     Address address, 
                                                     Religion religion, 
                                                     EducationData educationData) {
        return new PatientProfile(
            identity,
            birthData,
            age,
            gender,
            maritalStatus,
            address,
            religion,
            educationData,
            null,
            null,
            null
        );
    }

    /**
     * Crea un nuevo PatientProfile sin ID, pero con ReferredTherapist (backward compatibility).
     */
    public static PatientProfile createPatientProfile(Identity identity, 
                                                     BirthData birthData, 
                                                     Age age, 
                                                     Gender gender, 
                                                     MaritalStatus maritalStatus, 
                                                     Address address, 
                                                     Religion religion, 
                                                     EducationData educationData,
                                                     ReferredTherapist referredTherapist) {
        return new PatientProfile(
            identity,
            birthData,
            age,
            gender,
            maritalStatus,
            address,
            religion,
            educationData,
            referredTherapist,
            null,
            null
        );
    }

    /**
     * Crea un nuevo LegalResponsibleProfile sin ID (para que la base de datos lo genere).
     */
    public static LegalResponsibleProfile createLegalResponsibleProfile(Identity identity, 
                                                                        Relationship relationship) {
        return new LegalResponsibleProfile(
            identity,
            relationship
        );
    }

    /**
     * Crea un nuevo TherapistProfile sin ID (para que la base de datos lo genere).
     */
    public static TherapistProfile createTherapistProfile(Identity identity, 
                                                          Specialty specialty, 
                                                          AttentionPlace attentionPlace) {
        return new TherapistProfile(
            identity,
            specialty,
            attentionPlace
        );
    }

    /**
     * Método de conveniencia para crear una Identity completa.
     */
    public static Identity createIdentity(String firstNames,
                                         String paternalSurname,
                                         String maternalSurname,
                                         String documentNumber,
                                         DocumentType documentType,
                                         String phone,
                                         String email) {
        return new Identity(
            new FirstNames(firstNames),
            new PaternalSurname(paternalSurname),
            new MaternalSurname(maternalSurname),
            new IdentityDocumentNumber(documentNumber),
            documentType,
            new Phone(phone),
            new Email(email)
        );
    }

    /**
     * Método de conveniencia para crear BirthData.
     */
    public static BirthData createBirthData(String birthPlace, 
                                           java.time.LocalDate birthDate) {
        return new BirthData(birthPlace, birthDate);
    }

    /**
     * Método de conveniencia para crear Age.
     */
    public static Age createAge(int firstAppointmentAge, int currentAge) {
        return new Age(firstAppointmentAge, currentAge);
    }

    /**
     * Método de conveniencia para crear Address.
     */
    public static Address createAddress(String currentAddress,
                                       String district,
                                       String province,
                                       String region,
                                       String country) {
        return new Address(currentAddress, district, province, region, country);
    }

    /**
     * Método de conveniencia para crear EducationData.
     */
    public static EducationData createEducationData(String educationLevel,
                                                   String occupation,
                                                   String currentEducationalInstitution) {
        return new EducationData(educationLevel, occupation, currentEducationalInstitution);
    }

    /**
     * Método de conveniencia para crear Relationship.
     */
    public static Relationship createRelationship(String description) {
        return new Relationship(description);
    }

    /**
     * Método de conveniencia para crear Specialty.
     */
    public static Specialty createSpecialty(String name) {
        return new Specialty(name);
    }

    /**
     * Método de conveniencia para crear AttentionPlace.
     */
    public static AttentionPlace createAttentionPlace(String address) {
        return new AttentionPlace(address);
    }

    /**
     * Método de conveniencia para crear ReferredTherapist.
     */
    public static ReferredTherapist createReferredTherapist(String therapistName) {
        return new ReferredTherapist(therapistName);
    }
}