package com.soulware.therapysystem.profiles.profiles.domain.model.aggregates;

import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class PatientProfileTest {

    @Test
    void shouldCreatePatientProfileSuccessfully() {
        // Arrange: crear todos los value objects necesarios
        Identity identity = new Identity(
                new FirstNames("Juan"),
                new PaternalSurname("Perez"),
                new MaternalSurname("Lopez"),
                new IdentityDocumentNumber("12345678"),
                new DocumentType("DNI"),
                new Phone("999888777"),
                new Email("juan.perez@example.com")
        );

        BirthData birthData = new BirthData("Lima", LocalDate.of(1990, 5, 10));
        Age age = new Age(25, 35);
        Gender gender = new Gender("Masculino");
        MaritalStatus maritalStatus = new MaritalStatus("Soltero");
        Address address = new Address("Av. Los Olivos 123", "Los Olivos", "Lima", "Lima", "Perú");
        Religion religion = new Religion("Católica");
        EducationData educationData = new EducationData("Universitario", "Ingeniero", "PUCP");

        // Act: crear el paciente
        PatientProfile patient = new PatientProfile(identity, birthData, age, gender, maritalStatus, address, religion, educationData);

        // Assert: verificar que los datos se asignaron correctamente
        assertThat(patient.getIdentity().firstNames().value()).isEqualTo("Juan");
        assertThat(patient.getIdentity().paternalSurname().value()).isEqualTo("Perez");
        assertThat(patient.getIdentity().maternalSurname().value()).isEqualTo("Lopez");
        assertThat(patient.getIdentity().identityDocumentNumber().value()).isEqualTo("12345678");
        assertThat(patient.getAge().firstAppointment()).isEqualTo(25);
        assertThat(patient.getAge().current()).isEqualTo(35);
        assertThat(patient.getGender().value()).isEqualTo("Masculino");
        assertThat(patient.getMaritalStatus().value()).isEqualTo("Soltero");
        assertThat(patient.getAddress().currentAddress()).isEqualTo("Av. Los Olivos 123");
        assertThat(patient.getAddress().district()).isEqualTo("Los Olivos");
        assertThat(patient.getReligion().value()).isEqualTo("Católica");
        assertThat(patient.getEducationData().educationLevel()).isEqualTo("Universitario");
        assertThat(patient.getEducationData().occupation()).isEqualTo("Ingeniero");
        assertThat(patient.getEducationData().currentEducationalInstitution()).isEqualTo("PUCP");
    }
}