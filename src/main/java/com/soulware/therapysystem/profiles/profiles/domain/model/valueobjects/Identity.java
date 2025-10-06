package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class Identity {
    private final FirstNames firstNames;
    private final PaternalSurname paternalSurname;
    private final MaternalSurname maternalSurname;
    private final IdentityDocumentNumber identityDocumentNumber;
    private final DocumentType documentType;
    private final Phone phone;
    private final Email email;

    public Identity(FirstNames firstNames, 
                   PaternalSurname paternalSurname, 
                   MaternalSurname maternalSurname,
                   IdentityDocumentNumber identityDocumentNumber, 
                   DocumentType documentType, 
                   Phone phone, 
                   Email email) {
        if (firstNames == null) {
            throw new IllegalArgumentException("First names cannot be null");
        }
        if (paternalSurname == null) {
            throw new IllegalArgumentException("Paternal surname cannot be null");
        }
        if (maternalSurname == null) {
            throw new IllegalArgumentException("Maternal surname cannot be null");
        }
        if (identityDocumentNumber == null) {
            throw new IllegalArgumentException("Identity document number cannot be null");
        }
        if (documentType == null) {
            throw new IllegalArgumentException("Document type cannot be null");
        }
        if (phone == null) {
            throw new IllegalArgumentException("Phone cannot be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        // Validar coherencia entre tipo de documento y número
        validateDocumentTypeAndNumber(documentType, identityDocumentNumber);
        
        this.firstNames = firstNames;
        this.paternalSurname = paternalSurname;
        this.maternalSurname = maternalSurname;
        this.identityDocumentNumber = identityDocumentNumber;
        this.documentType = documentType;
        this.phone = phone;
        this.email = email;
    }

    private void validateDocumentTypeAndNumber(DocumentType documentType, IdentityDocumentNumber number) {
        String docType = documentType.value().toUpperCase();
        
        if ("DNI".equals(docType)) {
            if (!number.isValidDNI()) {
                throw new IllegalArgumentException("Invalid DNI format");
            }
        } else if ("RUC".equals(docType)) {
            if (!number.isValidRUC()) {
                throw new IllegalArgumentException("Invalid RUC format");
            }
        }
        // Para PASSPORT, OTHER y otros tipos, no validamos formato específico
    }

    public FirstNames firstNames() {
        return firstNames;
    }

    public PaternalSurname paternalSurname() {
        return paternalSurname;
    }

    public MaternalSurname maternalSurname() {
        return maternalSurname;
    }

    public IdentityDocumentNumber identityDocumentNumber() {
        return identityDocumentNumber;
    }

    public DocumentType documentType() {
        return documentType;
    }

    public Phone phone() {
        return phone;
    }

    public Email email() {
        return email;
    }

    public String fullName() {
        StringBuilder fullName = new StringBuilder();
        fullName.append(firstNames.value());
        fullName.append(" ").append(paternalSurname.value());
        
        if (!maternalSurname.isEmpty()) {
            fullName.append(" ").append(maternalSurname.value());
        }
        
        return fullName.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Identity identity = (Identity) obj;
        return Objects.equals(firstNames, identity.firstNames) &&
               Objects.equals(paternalSurname, identity.paternalSurname) &&
               Objects.equals(maternalSurname, identity.maternalSurname) &&
               Objects.equals(identityDocumentNumber, identity.identityDocumentNumber) &&
               Objects.equals(documentType, identity.documentType) &&
               Objects.equals(phone, identity.phone) &&
               Objects.equals(email, identity.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstNames, paternalSurname, maternalSurname, 
                           identityDocumentNumber, documentType, phone, email);
    }

    @Override
    public String toString() {
        return "Identity{" +
                "fullName='" + fullName() + '\'' +
                ", documentType=" + documentType +
                ", identityDocumentNumber=" + identityDocumentNumber +
                ", phone=" + phone +
                ", email=" + email +
                '}';
    }
}