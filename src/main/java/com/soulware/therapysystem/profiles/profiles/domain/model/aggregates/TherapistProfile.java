package com.soulware.therapysystem.profiles.profiles.domain.model.aggregates;

import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.*;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "therapist_profiles")
public class TherapistProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    // Identity fields
    @Column(name = "first_names", nullable = false)
    private String firstNames;
    
    @Column(name = "paternal_surname", nullable = false)
    private String paternalSurname;
    
    @Column(name = "maternal_surname")
    private String maternalSurname;
    
    @Column(name = "identity_document_number", nullable = false, unique = true)
    private String identityDocumentNumber;
    
    @Column(name = "document_type", nullable = false)
    private String documentType;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "specialty_name", nullable = false)
    private String specialtyName;
    
    @Column(name = "attention_place_address", nullable = false)
    private String attentionPlaceAddress;

    // JPA default constructor
    protected TherapistProfile() {}

    // Constructor sin ID para nuevas entidades (ID generado por DB)
    public TherapistProfile(Identity identity, 
                           Specialty specialty, 
                           AttentionPlace attentionPlace) {
        if (identity == null) {
            throw new IllegalArgumentException("Identity cannot be null");
        }
        if (specialty == null) {
            throw new IllegalArgumentException("Specialty cannot be null");
        }
        if (attentionPlace == null) {
            throw new IllegalArgumentException("Attention place cannot be null");
        }

        this.id = null; // Será generado por la base de datos
        this.firstNames = identity.firstNames().value();
        this.paternalSurname = identity.paternalSurname().value();
        this.maternalSurname = identity.maternalSurname() != null ? identity.maternalSurname().value() : null;
        this.identityDocumentNumber = identity.identityDocumentNumber().value();
        this.documentType = identity.documentType().value();
        this.phone = identity.phone().value();
        this.email = identity.email().value();
        this.specialtyName = specialty.name();
        this.attentionPlaceAddress = attentionPlace.address();
    }

    // Método de negocio: actualizar especialidad
    public void updateSpecialty(Specialty newSpecialty) {
        if (newSpecialty == null) {
            throw new IllegalArgumentException("New specialty cannot be null");
        }
        this.specialtyName = newSpecialty.name();
    }

    // Método de negocio: actualizar lugar de atención
    public void updateAttentionPlace(AttentionPlace newAttentionPlace) {
        if (newAttentionPlace == null) {
            throw new IllegalArgumentException("New attention place cannot be null");
        }
        this.attentionPlaceAddress = newAttentionPlace.address();
    }

    // Getters
    public TherapistProfileId getId() {
        return this.id != null ? new TherapistProfileId(this.id) : null;
    }

    public Identity getIdentity() {
        return new Identity(
            new FirstNames(this.firstNames),
            new PaternalSurname(this.paternalSurname),
            this.maternalSurname != null ? new MaternalSurname(this.maternalSurname) : null,
            new IdentityDocumentNumber(this.identityDocumentNumber),
            new DocumentType(this.documentType),
            new Phone(this.phone),
            new Email(this.email)
        );
    }

    public Specialty getSpecialty() {
        return new Specialty(this.specialtyName);
    }

    public AttentionPlace getAttentionPlace() {
        return new AttentionPlace(this.attentionPlaceAddress);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TherapistProfile that = (TherapistProfile) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TherapistProfile{" +
                "id=" + id +
                ", firstNames='" + firstNames + '\'' +
                ", specialtyName='" + specialtyName + '\'' +
                ", attentionPlaceAddress='" + attentionPlaceAddress + '\'' +
                '}';
    }
}