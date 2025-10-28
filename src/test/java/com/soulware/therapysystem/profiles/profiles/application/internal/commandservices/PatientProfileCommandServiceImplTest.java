package com.soulware.therapysystem.profiles.profiles.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.*;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreatePatientProfileCommand;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories.PatientProfileRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

class PatientProfileCommandServiceIntegrationTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private PatientProfileRepositoryImpl patientProfileRepository;
    private PatientProfileCommandServiceImpl patientProfileCommandService;

    @BeforeAll
    static void init() {
        // Crea el EntityManagerFactory para H2 (usando persistence.xml)
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        patientProfileRepository = new PatientProfileRepositoryImpl(em);
        patientProfileCommandService = new PatientProfileCommandServiceImpl(patientProfileRepository);
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) em.close();
    }

    @AfterAll
    static void close() {
        emf.close();
    }

    @Test
    void shouldPersistPatientProfileSuccessfully() {
        // Arrange
        CreatePatientProfileCommand command = new CreatePatientProfileCommand(
                "Juan",
                "Perez",
                "Lopez",
                "12345678",
                "DNI",
                "999888777",
                "juan.perez@example.com",
                "Lima",
                "1990-05-10",
                25,
                35,
                "Masculino",
                "Soltero",
                "Av. Los Olivos 123",
                "Los Olivos",
                "Lima",
                "Lima",
                "Perú",
                "Católica",
                "Universitario",
                "Ingeniero",
                "PUCP",
                "Jesús",
                1,
                1
        );

        em.getTransaction().begin();

        // Act
        Optional<PatientProfile> result = patientProfileCommandService.handle(command);

        em.getTransaction().commit();

        // Assert
        assertTrue(result.isPresent());
        PatientProfile saved = result.get();
        PatientProfile persisted = em.find(PatientProfile.class, saved.getId().value());

        assertNotNull(persisted);
        assertEquals("Juan", persisted.getIdentity().firstNames().value());
        assertEquals("Ingeniero", persisted.getEducationData().occupation());
    }
}
