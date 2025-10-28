package com.soulware.therapysystem.profiles.profiles.application.internal.commandservices;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreatePatientProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.DeletePatientProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.AssignLegalResponsibleCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.AssignTherapistCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.factories.ProfileFactory;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.*;
import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.model.repositories.PatientProfileRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class PatientProfileCommandServiceImpl implements PatientProfileCommandService {

    private final PatientProfileRepository patientProfileRepository;

    @Inject
    public PatientProfileCommandServiceImpl(PatientProfileRepository patientProfileRepository) {
        this.patientProfileRepository = patientProfileRepository;
    }

    @Override
    public Optional<PatientProfile> handle(CreatePatientProfileCommand command) {
        // Create Identity using factory
        Identity identity = ProfileFactory.createIdentity(
            command.firstNames(),
            command.paternalSurname(),
            command.maternalSurname(),
            command.identityDocumentNumber(),
            new DocumentType(command.documentType()),
            command.phone(),
            command.email()
        );

        // Create BirthData
        BirthData birthData = ProfileFactory.createBirthData(
            command.birthPlace(),
            LocalDate.parse(command.birthDate())
        );

        // Create Age
        Age age = ProfileFactory.createAge(
            command.firstAppointmentAge(),
            command.currentAge()
        );

        // Create Address
        Address address = ProfileFactory.createAddress(
            command.currentAddress(),
            command.district(),
            command.province(),
            command.region(),
            command.country()
        );

        // Create EducationData
        EducationData educationData = ProfileFactory.createEducationData(
            command.educationLevel(),
            command.occupation(),
            command.currentEducationalInstitution()
        );

        // Create ReferredTherapist if data is provided
        ReferredTherapist referredTherapist = null;
        if (command.referredTherapistName() != null && !command.referredTherapistName().trim().isEmpty()) {
            referredTherapist = ProfileFactory.createReferredTherapist(command.referredTherapistName());
        }

        // Create PatientProfile using factory (sin ID, será generado por la DB)
        PatientProfile patientProfile = ProfileFactory.createPatientProfile(
            identity,
            birthData,
            age,
            new Gender(command.gender()),
            new MaritalStatus(command.maritalStatus()),
            address,
            new Religion(command.religion()),
            educationData,
            referredTherapist,
            command.legalResponsibleId(),
            command.therapistId()
        );

        // Save using repository - let domain/infrastructure exceptions propagate
        PatientProfile saved = patientProfileRepository.save(patientProfile);
        return Optional.of(saved);
    }

    @Override
    public boolean handle(DeletePatientProfileCommand command) {
        try {
            PatientProfileId id = new PatientProfileId(command.patientProfileId());
            patientProfileRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            // Log error
            System.err.println("Error deleting patient profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<PatientProfile> handle(AssignLegalResponsibleCommand command) {
        try {
            PatientProfileId id = new PatientProfileId(command.patientProfileId());
            Optional<PatientProfile> patientOpt = patientProfileRepository.findById(id);
            
            if (patientOpt.isPresent()) {
                PatientProfile patient = patientOpt.get();
                patient.assignLegalResponsible(command.legalResponsibleId());
                PatientProfile updated = patientProfileRepository.save(patient);
                return Optional.of(updated);
            }
            
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error assigning legal responsible to patient profile: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<PatientProfile> handle(AssignTherapistCommand command) {
        try {
            PatientProfileId id = new PatientProfileId(command.patientProfileId());
            Optional<PatientProfile> patientOpt = patientProfileRepository.findById(id);
            
            if (patientOpt.isPresent()) {
                PatientProfile patient = patientOpt.get();
                patient.assignTherapist(command.therapistId());
                PatientProfile updated = patientProfileRepository.save(patient);
                return Optional.of(updated);
            }
            
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error assigning therapist to patient profile: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
}