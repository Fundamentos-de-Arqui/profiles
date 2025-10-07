package com.soulware.therapysystem.profiles.profiles.domain.model.aggregates;

import static org.junit.jupiter.api.Assertions.*;

import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LegalResponsibleProfileTest {

    @Test
    void shouldCreateLegalResponsibleProfileSuccessfully() {
        // Arrange
        Identity identity = new Identity(
                new FirstNames("Carlos"),
                new PaternalSurname("Martinez"),
                new MaternalSurname("Diaz"),
                new IdentityDocumentNumber("11223344"),
                new DocumentType("DNI"),
                new Phone("912345678"),
                new Email("carlos.martinez@example.com")
        );
        Relationship relationship = new Relationship("Padre");

        // Act
        LegalResponsibleProfile legalResponsible = new LegalResponsibleProfile(identity, relationship);

        // Assert
        assertThat(legalResponsible).isNotNull();
        assertThat(legalResponsible.getIdentity().firstNames().value()).isEqualTo("Carlos");
        assertThat(legalResponsible.getIdentity().paternalSurname().value()).isEqualTo("Martinez");
        assertThat(legalResponsible.getRelationship().description()).isEqualTo("Padre");
    }
}
