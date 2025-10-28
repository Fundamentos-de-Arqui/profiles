package com.soulware.therapysystem.profiles.profiles.application.internal.queryservices;

import com.soulware.therapysystem.profiles.profiles.application.internal.commandservices.TherapistProfileCommandServiceImpl;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateTherapistProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetTherapistProfileByDocumentQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetTherapistProfilesBySpecialtyQuery;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories.TherapistProfileRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TherapistProfileQueryServiceIntegrationTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private TherapistProfileRepositoryImpl therapistProfileRepository;
    private TherapistProfileCommandServiceImpl therapistProfileCommandService;
    private TherapistProfileQueryServiceImpl therapistProfileQueryService;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        therapistProfileRepository = new TherapistProfileRepositoryImpl(em);
        therapistProfileCommandService = new TherapistProfileCommandServiceImpl(therapistProfileRepository);
        therapistProfileQueryService = new TherapistProfileQueryServiceImpl(therapistProfileRepository);
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
    void shouldReturnTherapistProfileWhenDocumentExists() {
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
        therapistProfileCommandService.handle(command);
        em.getTransaction().commit();

        // Act
        GetTherapistProfileByDocumentQuery query = new GetTherapistProfileByDocumentQuery("DNI", "87654321");
        Optional<TherapistProfile> result = therapistProfileQueryService.handle(query);

        // Assert
        assertTrue(result.isPresent());
        TherapistProfile therapist = result.get();
        assertEquals("Ana", therapist.getIdentity().firstNames().value());
        assertEquals("Psicología", therapist.getSpecialty().name());
    }

    @Test
    void shouldReturnEmptyWhenDocumentDoesNotExist() {
        // Arrange
        GetTherapistProfileByDocumentQuery query = new GetTherapistProfileByDocumentQuery("DNI", "00000000");

        // Act
        Optional<TherapistProfile> result = therapistProfileQueryService.handle(query);

        // Assert
        assertTrue(result.isEmpty());
    }
}

class TherapistProfileQueryBySpecialtyIntegrationTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private TherapistProfileRepositoryImpl therapistProfileRepository;
    private TherapistProfileCommandServiceImpl therapistProfileCommandService;
    private TherapistProfileQueryServiceImpl therapistProfileQueryService;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        therapistProfileRepository = new TherapistProfileRepositoryImpl(em);
        therapistProfileCommandService = new TherapistProfileCommandServiceImpl(therapistProfileRepository);
        therapistProfileQueryService = new TherapistProfileQueryServiceImpl(therapistProfileRepository);
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
    void shouldReturnTherapistsBySpecialty() {
        // Arrange
        CreateTherapistProfileCommand therapist1 = new CreateTherapistProfileCommand(
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

        CreateTherapistProfileCommand therapist2 = new CreateTherapistProfileCommand(
                "Pedro",
                "Ramirez",
                "Soto",
                "12349876",
                "DNI",
                "977665544",
                "pedro.ramirez@example.com",
                "Psicología",
                "Calle Primavera 789"
        );

        CreateTherapistProfileCommand therapist3 = new CreateTherapistProfileCommand(
                "Lucia",
                "Torres",
                "Diaz",
                "56781234",
                "DNI",
                "944332211",
                "lucia.torres@example.com",
                "Terapia Ocupacional",
                "Av. Central 101"
        );

        em.getTransaction().begin();
        therapistProfileCommandService.handle(therapist1);
        therapistProfileCommandService.handle(therapist2);
        therapistProfileCommandService.handle(therapist3);
        em.getTransaction().commit();

        // Act
        GetTherapistProfilesBySpecialtyQuery query = new GetTherapistProfilesBySpecialtyQuery("Psicología");
        List<TherapistProfile> result = therapistProfileQueryService.handle(query);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getSpecialty().name().equals("Psicología")));
    }

    @Test
    void shouldReturnEmptyListWhenNoTherapistsFoundForSpecialty() {
        // Arrange
        GetTherapistProfilesBySpecialtyQuery query = new GetTherapistProfilesBySpecialtyQuery("Neurología");

        // Act
        List<TherapistProfile> result = therapistProfileQueryService.handle(query);

        // Assert
        assertTrue(result.isEmpty());
    }
}
