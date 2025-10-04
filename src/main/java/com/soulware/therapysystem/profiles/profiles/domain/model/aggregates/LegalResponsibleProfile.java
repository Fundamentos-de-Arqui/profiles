package com.soulware.therapysystem.profiles.profiles.domain.model.aggregates;

import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.*;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "legal_responsible_profiles")
public class LegalResponsibleProfile {
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
    
    @Column(name = "relationship", nullable = false)
    private String relationship;

    // JPA default constructor
    protected LegalResponsibleProfile() {}

    // Constructor sin ID para nuevas entidades (ID generado por DB)
    public LegalResponsibleProfile(Identity identity, 
                                  Relationship relationship) {
        if (identity == null) {
            throw new IllegalArgumentException("Identity cannot be null");
        }
        if (relationship == null) {
            throw new IllegalArgumentException("Relationship cannot be null");
        }

        this.id = null; // Será generado por la base de datos
        this.firstNames = identity.firstNames().value();
        this.paternalSurname = identity.paternalSurname().value();
        this.maternalSurname = identity.maternalSurname() != null ? identity.maternalSurname().value() : null;
        this.identityDocumentNumber = identity.identityDocumentNumber().value();
        this.documentType = identity.documentType().name();
        this.phone = identity.phone().value();
        this.email = identity.email().value();
        this.relationship = relationship.description();
    }

    // Método de negocio: cambiar relación
    public void changeRelationship(Relationship newRelationship) {
        if (newRelationship == null) {
            throw new IllegalArgumentException("New relationship cannot be null");
        }
        this.relationship = newRelationship.description();
    }

    // Getters
    public LegalResponsibleProfileId getId() {
        return this.id != null ? new LegalResponsibleProfileId(this.id) : null;
    }

    public Identity getIdentity() {
        return new Identity(
            new FirstNames(this.firstNames),
            new PaternalSurname(this.paternalSurname),
            this.maternalSurname != null ? new MaternalSurname(this.maternalSurname) : null,
            new IdentityDocumentNumber(this.identityDocumentNumber),
            DocumentType.valueOf(this.documentType.toUpperCase()),
            new Phone(this.phone),
            new Email(this.email)
        );
    }

    public Relationship getRelationship() {
        return new Relationship(this.relationship);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LegalResponsibleProfile that = (LegalResponsibleProfile) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LegalResponsibleProfile{" +
                "id=" + id +
                ", firstNames='" + firstNames + '\'' +
                ", relationship='" + relationship + '\'' +
                '}';
    }
}