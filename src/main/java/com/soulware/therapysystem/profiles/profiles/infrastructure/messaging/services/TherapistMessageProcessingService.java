package com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.services;

import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateTherapistProfileCommand;
import com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto.TherapistProcessingMessage;
import com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto.SimpleTherapistData;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.logging.Logger;

/**
 * Service for processing therapist messages received from external systems
 */
@ApplicationScoped
public class TherapistMessageProcessingService {
    
    private static final Logger logger = Logger.getLogger(TherapistMessageProcessingService.class.getName());
    
    @Inject
    private TherapistProfileCommandService therapistCommandService;
    
    /**
     * Processes a complete therapist message from external systems
     * Creates therapist profile in the database
     */
    @Transactional
    public void processTherapistMessage(SimpleTherapistData therapistData) {
        try {
            logger.info("Processing therapist message for document: " + therapistData.identityDocumentNumber());
            
            // Create therapist profile
            var therapistCommand = createTherapistCommand(therapistData);
            var therapistProfileOpt = therapistCommandService.handle(therapistCommand);
            
            if (therapistProfileOpt.isEmpty()) {
                throw new RuntimeException("Failed to create therapist profile");
            }
            
            var therapistProfile = therapistProfileOpt.get();
            logger.info("Created therapist profile with ID: " + therapistProfile.getId().value());
            
            logger.info("Successfully processed therapist message for document: " + therapistData.identityDocumentNumber());
            
        } catch (Exception e) {
            logger.severe("Error processing therapist message: " + e.getMessage());
            throw new RuntimeException("Failed to process therapist message", e);
        }
    }
    
    /**
     * Process a complete therapist processing message
     */
    @Transactional
    public void processTherapistMessage(TherapistProcessingMessage message) {
        try {
            logger.info("Processing therapist processing message with ID: " + message.messageId());
            processTherapistMessage(message.therapistData());
        } catch (Exception e) {
            logger.severe("Error processing therapist processing message: " + e.getMessage());
            throw new RuntimeException("Failed to process therapist processing message", e);
        }
    }
    
    private CreateTherapistProfileCommand createTherapistCommand(SimpleTherapistData data) {
        return new CreateTherapistProfileCommand(
            data.firstNames(),
            data.paternalSurname(),
            data.maternalSurname(),
            data.identityDocumentNumber(),      // identityDocumentNumber
            data.documentType(),
            data.phone(),
            data.email(),
            data.specialtyName() != null ? data.specialtyName() : "No especificado",         // specialtyName
            data.attentionPlaceAddress() != null ? data.attentionPlaceAddress() : ""         // attentionPlaceAddress
        );
    }
}