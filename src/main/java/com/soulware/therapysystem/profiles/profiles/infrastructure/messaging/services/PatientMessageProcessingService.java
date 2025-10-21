package com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.services;

import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreatePatientProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateTherapistProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateLegalResponsibleProfileCommand;
import com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto.CompletePatientDataRequest;
import com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto.LegalResponsibleData;
import com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto.TherapistData;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.logging.Logger;

/**
 * Service for processing patient messages received from CustomerService
 */
@ApplicationScoped
public class PatientMessageProcessingService {
    
    private static final Logger logger = Logger.getLogger(PatientMessageProcessingService.class.getName());
    
    @Inject
    private PatientProfileCommandService patientCommandService;
    
    @Inject
    private TherapistProfileCommandService therapistCommandService;
    
    @Inject
    private LegalResponsibleProfileCommandService legalResponsibleCommandService;
    
    /**
     * Processes a complete patient data message from CustomerService
     * Creates patient profile and associated therapist/legal responsible profiles
     */
    @Transactional
    public void processPatientMessage(CompletePatientDataRequest patientData) {
        try {
            logger.info("Processing patient message for document: " + patientData.documentNumber());
            
            // Create patient profile
            var patientCommand = createPatientCommand(patientData);
            var patientProfileOpt = patientCommandService.handle(patientCommand);
            if (patientProfileOpt.isEmpty()) {
                throw new RuntimeException("Failed to create patient profile");
            }
            var patientProfile = patientProfileOpt.get();
            logger.info("Created patient profile with ID: " + patientProfile.getId().value());
            
            // Create associated therapist profiles
            if (patientData.therapists() != null) {
                for (TherapistData therapistData : patientData.therapists()) {
                    var therapistCommand = createTherapistCommand(therapistData);
                    var therapistProfileOpt = therapistCommandService.handle(therapistCommand);
                    if (therapistProfileOpt.isEmpty()) {
                        logger.warning("Failed to create therapist profile for: " + therapistData.documentNumber());
                        continue;
                    }
                    var therapistProfile = therapistProfileOpt.get();
                    logger.info("Created therapist profile with ID: " + therapistProfile.getId().value());
                }
            }
            
            // Create associated legal responsible profiles
            if (patientData.legalResponsibles() != null) {
                for (LegalResponsibleData legalData : patientData.legalResponsibles()) {
                    var legalCommand = createLegalResponsibleCommand(legalData);
                    var legalProfileOpt = legalResponsibleCommandService.handle(legalCommand);
                    if (legalProfileOpt.isEmpty()) {
                        logger.warning("Failed to create legal responsible profile for: " + legalData.documentNumber());
                        continue;
                    }
                    var legalProfile = legalProfileOpt.get();
                    logger.info("Created legal responsible profile with ID: " + legalProfile.getId().value());
                }
            }
            
            logger.info("Successfully processed patient message for document: " + patientData.documentNumber());
            
        } catch (Exception e) {
            logger.severe("Error processing patient message: " + e.getMessage());
            throw new RuntimeException("Failed to process patient message", e);
        }
    }
    
    private CreatePatientProfileCommand createPatientCommand(CompletePatientDataRequest data) {
        return new CreatePatientProfileCommand(
            data.firstNames(),
            data.paternalSurname(),
            data.maternalSurname(),
            data.documentNumber(),  // identityDocumentNumber
            data.documentType(),
            data.phone(),
            data.email(),
            data.birthPlace() != null ? data.birthPlace() : "No especificado", // Handle null birthPlace
            data.birthDate().toString(),  // birthDate as String
            data.ageFirstAppointment() != null ? data.ageFirstAppointment() : 0,   // Handle null age
            data.ageCurrent() != null ? data.ageCurrent() : 0,
            data.gender() != null ? data.gender() : "No especificado",
            data.maritalStatus() != null ? data.maritalStatus() : "No especificado",
            data.currentAddress() != null ? data.currentAddress() : "No especificado",
            data.district() != null ? data.district() : "No especificado",
            data.province() != null ? data.province() : "No especificado",
            data.region() != null ? data.region() : "No especificado",
            data.country() != null ? data.country() : "No especificado",
            data.religion() != null ? data.religion() : "No especificado",
            data.educationLevel() != null ? data.educationLevel() : "No especificado",
            data.occupation() != null ? data.occupation() : "No especificado",
            data.currentEducationalInstitution() != null ? data.currentEducationalInstitution() : "No especificado",
            data.referredTherapistName()
        );
    }
    
    private CreateTherapistProfileCommand createTherapistCommand(TherapistData data) {
        return new CreateTherapistProfileCommand(
            data.firstNames(),
            data.paternalSurname(),
            data.maternalSurname(),
            data.documentNumber(),      // identityDocumentNumber
            data.documentType(),
            data.phone(),
            data.email(),
            data.specialties(),         // specialtyName
            ""                          // attentionPlaceAddress - not provided in CustomerService data
        );
    }
    
    private CreateLegalResponsibleProfileCommand createLegalResponsibleCommand(LegalResponsibleData data) {
        return new CreateLegalResponsibleProfileCommand(
            data.firstNames(),
            data.paternalSurname(),
            data.maternalSurname(),
            data.documentNumber(),      // identityDocumentNumber
            data.documentType(),
            data.phone(),
            data.email(),
            data.relationship()
        );
    }
}