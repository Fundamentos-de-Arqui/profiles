package com.soulware.therapysystem.profiles.profiles.domain.model.aggregates;

import static org.junit.jupiter.api.Assertions.*;

import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TherapistProfileTest {

    @Test
    void shouldCreateTherapistProfileSuccessfully() {
        // Arrange
        Identity identity = new Identity(
                new FirstNames("Ana"),
                new PaternalSurname("Gomez"),
                new MaternalSurname("Rojas"),
                new IdentityDocumentNumber("87654321"),
                new DocumentType("DNI"),
                new Phone("987654321"),
                new Email("ana.gomez@example.com")
        );
        Specialty specialty = new Specialty("Fisioterapia");
        AttentionPlace attentionPlace = new AttentionPlace("Av. Central 456, Lima");

        // Act
        TherapistProfile therapist = new TherapistProfile(identity, specialty, attentionPlace);

        // Assert
        assertThat(therapist).isNotNull();
        assertThat(therapist.getIdentity().firstNames().value()).isEqualTo("Ana");
        assertThat(therapist.getIdentity().paternalSurname().value()).isEqualTo("Gomez");
        assertThat(therapist.getSpecialty().name()).isEqualTo("Fisioterapia");
        assertThat(therapist.getAttentionPlace().address()).isEqualTo("Av. Central 456, Lima");
    }
}
