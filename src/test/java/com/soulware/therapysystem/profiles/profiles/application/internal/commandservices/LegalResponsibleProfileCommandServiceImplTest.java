package com.soulware.therapysystem.profiles.profiles.application.internal.commandservices;


import static org.junit.jupiter.api.Assertions.*;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateLegalResponsibleProfileCommand;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories.LegalResponsibleProfileRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import java.util.Optional;

class LegalResponsibleProfileCommandServiceIntegrationTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private LegalResponsibleProfileRepositoryImpl legalResponsibleProfileRepository;
    private LegalResponsibleProfileCommandServiceImpl legalResponsibleProfileCommandService;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        legalResponsibleProfileRepository = new LegalResponsibleProfileRepositoryImpl(em);
        legalResponsibleProfileCommandService = new LegalResponsibleProfileCommandServiceImpl(legalResponsibleProfileRepository);
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
    void shouldPersistLegalResponsibleProfileSuccessfully() {
        // Arrange
        CreateLegalResponsibleProfileCommand command = new CreateLegalResponsibleProfileCommand(
                "Maria",
                "Gomez",
                "Lopez",
                "87654321",
                "DNI",
                "988777666",
                "maria.gomez@example.com",
                "Madre"
        );

        em.getTransaction().begin();

        // Act
        Optional<LegalResponsibleProfile> result = legalResponsibleProfileCommandService.handle(command);

        em.getTransaction().commit();

        // Assert
        assertTrue(result.isPresent());
        LegalResponsibleProfile saved = result.get();
        LegalResponsibleProfile persisted = em.find(LegalResponsibleProfile.class, saved.getId().value());

        assertNotNull(persisted);
        assertEquals("Maria", persisted.getIdentity().firstNames().value());
        assertEquals("Madre", persisted.getRelationship().description());
    }
}