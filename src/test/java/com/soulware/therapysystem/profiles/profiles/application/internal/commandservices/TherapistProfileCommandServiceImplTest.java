package com.soulware.therapysystem.profiles.profiles.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.*;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateTherapistProfileCommand;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories.TherapistProfileRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.Optional;

class TherapistProfileCommandServiceIntegrationTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private TherapistProfileRepositoryImpl therapistProfileRepository;
    private TherapistProfileCommandServiceImpl therapistProfileCommandService;

    @BeforeAll
    static void init() {
        // EntityManagerFactory para H2
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        therapistProfileRepository = new TherapistProfileRepositoryImpl(em);
        therapistProfileCommandService = new TherapistProfileCommandServiceImpl(therapistProfileRepository);
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
    void shouldPersistTherapistProfileSuccessfully() {
        // Arrange
        CreateTherapistProfileCommand command = new CreateTherapistProfileCommand(
                "Ana",
                "Gomez",
                "Lopez",
                "87654321",
                "DNI",
                "988776655",
                "ana.gomez@example.com",
                "Psicología",
                "Av. Los Olivos 456"
        );

        em.getTransaction().begin();

        // Act
        Optional<TherapistProfile> result = therapistProfileCommandService.handle(command);

        em.getTransaction().commit();

        // Assert
        assertTrue(result.isPresent());
        TherapistProfile saved = result.get();
        TherapistProfile persisted = em.find(TherapistProfile.class, saved.getId().value());

        assertNotNull(persisted);
        assertEquals("Ana", persisted.getIdentity().firstNames().value());
        assertEquals("Psicología", persisted.getSpecialty().name());
        assertEquals("Av. Los Olivos 456", persisted.getAttentionPlace().address());
    }
}
