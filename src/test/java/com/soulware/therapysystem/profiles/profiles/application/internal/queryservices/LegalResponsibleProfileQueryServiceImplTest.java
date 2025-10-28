package com.soulware.therapysystem.profiles.profiles.application.internal.queryservices;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateLegalResponsibleProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetLegalResponsibleProfileByDocumentQuery;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories.LegalResponsibleProfileRepositoryImpl;
import com.soulware.therapysystem.profiles.profiles.application.internal.commandservices.LegalResponsibleProfileCommandServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LegalResponsibleProfileQueryServiceIntegrationTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private LegalResponsibleProfileRepositoryImpl legalResponsibleProfileRepository;
    private LegalResponsibleProfileCommandServiceImpl legalResponsibleProfileCommandService;
    private LegalResponsibleProfileQueryServiceImpl legalResponsibleProfileQueryService;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        legalResponsibleProfileRepository = new LegalResponsibleProfileRepositoryImpl(em);
        legalResponsibleProfileCommandService = new LegalResponsibleProfileCommandServiceImpl(legalResponsibleProfileRepository);
        legalResponsibleProfileQueryService = new LegalResponsibleProfileQueryServiceImpl(legalResponsibleProfileRepository);
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
    void shouldReturnLegalResponsibleProfileByDocumentSuccessfully() {
        // Arrange - primero creamos y persistimos un perfil
        CreateLegalResponsibleProfileCommand createCommand = new CreateLegalResponsibleProfileCommand(
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
        legalResponsibleProfileCommandService.handle(createCommand);
        em.getTransaction().commit();

        // Act - luego consultamos usando el documento
        GetLegalResponsibleProfileByDocumentQuery query =
                new GetLegalResponsibleProfileByDocumentQuery("DNI", "87654321");

        Optional<LegalResponsibleProfile> result = legalResponsibleProfileQueryService.handle(query);

        // Assert
        assertTrue(result.isPresent());
        LegalResponsibleProfile found = result.get();
        assertEquals("Maria", found.getIdentity().firstNames().value());
        assertEquals("Madre", found.getRelationship().description());
    }

    @Test
    void shouldReturnEmptyWhenDocumentNotFound() {
        // Act
        GetLegalResponsibleProfileByDocumentQuery query =
                new GetLegalResponsibleProfileByDocumentQuery("DNI", "99999999");

        Optional<LegalResponsibleProfile> result = legalResponsibleProfileQueryService.handle(query);

        // Assert
        assertTrue(result.isEmpty());
    }
}
