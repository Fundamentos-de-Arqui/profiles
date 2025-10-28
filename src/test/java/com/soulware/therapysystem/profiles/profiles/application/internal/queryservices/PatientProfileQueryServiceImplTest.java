package com.soulware.therapysystem.profiles.profiles.application.internal.queryservices;

import com.soulware.therapysystem.profiles.profiles.application.internal.commandservices.PatientProfileCommandServiceImpl;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreatePatientProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetPatientProfileByDocumentQuery;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories.LegalResponsibleProfileRepositoryImpl;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories.PatientProfileRepositoryImpl;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories.TherapistProfileRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PatientProfileQueryServiceIntegrationTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private PatientProfileRepositoryImpl patientProfileRepository;
    private LegalResponsibleProfileQueryServiceImpl legalResponsibleProfileQueryServiceImpl;
    private TherapistProfileQueryServiceImpl therapistProfileQueryServiceImpl;
    private PatientProfileCommandServiceImpl patientProfileCommandService;
    private PatientProfileQueryServiceImpl patientProfileQueryService;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        patientProfileRepository = new PatientProfileRepositoryImpl(em);
        legalResponsibleProfileQueryServiceImpl = new LegalResponsibleProfileQueryServiceImpl(new LegalResponsibleProfileRepositoryImpl(em));
        therapistProfileQueryServiceImpl = new TherapistProfileQueryServiceImpl(new TherapistProfileRepositoryImpl(em));
        patientProfileCommandService = new PatientProfileCommandServiceImpl(patientProfileRepository);
        patientProfileQueryService = new PatientProfileQueryServiceImpl(patientProfileRepository,  legalResponsibleProfileQueryServiceImpl, therapistProfileQueryServiceImpl);
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
    void shouldReturnPatientProfileWhenDocumentExists() {
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
        patientProfileCommandService.handle(command);
        em.getTransaction().commit();

        // Act
        GetPatientProfileByDocumentQuery query = new GetPatientProfileByDocumentQuery("DNI", "12345678");
        Optional<PatientProfile> result = patientProfileQueryService.handle(query);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getIdentity().firstNames().value());
    }

    @Test
    void shouldReturnEmptyWhenDocumentDoesNotExist() {
        // Arrange
        GetPatientProfileByDocumentQuery query = new GetPatientProfileByDocumentQuery("DNI", "00000000");

        // Act
        Optional<PatientProfile> result = patientProfileQueryService.handle(query);

        // Assert
        assertTrue(result.isEmpty());
    }
}
