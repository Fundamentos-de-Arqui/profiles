package com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.listeners;
import org.apache.activemq.ActiveMQConnectionFactory;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.enterprise.inject.spi.CDI;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Optional;

import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileQueryService;

import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreatePatientProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateTherapistProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateLegalResponsibleProfileCommand;

import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetPatientProfileByDocumentQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetTherapistProfileByDocumentQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetLegalResponsibleProfileByDocumentQuery;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;

@WebListener
public class ProfileRegisterActiveMQListener implements ServletContextListener, MessageListener {
    private static final Logger logger = Logger.getLogger(ProfileRegisterActiveMQListener.class.getName());
    private static Connection connection;
    private static Session session;
    private static MessageConsumer consumer;
    private static MessageProducer producer;
    private static boolean isInitialized = false;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        synchronized (ProfileRegisterActiveMQListener.class) {
            if (isInitialized) {
                logger.info("ProfileRegisterActiveMQListener already initialized, skipping...");
                return;
            }
            try {
                closeExistingConnections();
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                connection = connectionFactory.createConnection();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue inputQueue = session.createQueue("profiles_register");
                Queue outputQueue = session.createQueue("iam_register");
                consumer = session.createConsumer(inputQueue);
                producer = session.createProducer(outputQueue);
                consumer.setMessageListener(this);
                connection.start();
                isInitialized = true;
                logger.info("SUCCESS: ProfileRegisterActiveMQListener connected to profiles_register and ready to send to iam_register");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "FAILED to initialize ProfileRegisterActiveMQListener: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== ProfileRegisterActiveMQListener STOPPING ===");
        closeExistingConnections();
    }

    private static void closeExistingConnections() {
        try {
            if (consumer != null) { consumer.close(); consumer = null; }
            if (producer != null) { producer.close(); producer = null; }
            if (session != null) { session.close(); session = null; }
            if (connection != null) { connection.close(); connection = null; }
            isInitialized = false;
            logger.info("Connections closed successfully");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error closing connections: " + e.getMessage(), e);
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            logger.info("=== MESSAGE RECEIVED BY PROFILE REGISTER LISTENER ===");
            String messageText = extractMessageText(message);
            if (messageText == null) return;

            Jsonb jsonb = JsonbBuilder.create();
            
            // Try to parse as different profile types by checking required fields
            try {
                // Check if it's a patient profile (has birthDate, currentAge, etc.)
                if (messageText.contains("birthDate") && messageText.contains("currentAge")) {
                    PatientRegisterRequest request = jsonb.fromJson(messageText, PatientRegisterRequest.class);
                    processPatientRegistration(request, jsonb);
                }
                // Check if it's a therapist profile (has specialtyName)
                else if (messageText.contains("specialtyName")) {
                    TherapistRegisterRequest request = jsonb.fromJson(messageText, TherapistRegisterRequest.class);
                    processTherapistRegistration(request, jsonb);
                }
                // Check if it's a legal responsible (has relationship)
                else if (messageText.contains("relationship")) {
                    LegalResponsibleRegisterRequest request = jsonb.fromJson(messageText, LegalResponsibleRegisterRequest.class);
                    processLegalResponsibleRegistration(request, jsonb);
                }
                else {
                    logger.warning("Unknown profile type in registration message: " + messageText);
                }
            } catch (Exception parseEx) {
                logger.log(Level.WARNING, "Error parsing registration message: " + parseEx.getMessage(), parseEx);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing profile registration message: " + e.getMessage(), e);
        }
    }

    private String extractMessageText(Message message) throws Exception {
        String messageText = null;
        if (message instanceof TextMessage textMessage) {
            messageText = textMessage.getText();
            logger.info("Received TEXT message: " + messageText);
        } else if (message instanceof BytesMessage bytesMessage) {
            logger.info("Received BYTES message");
            long length = bytesMessage.getBodyLength();
            if (length > 0) {
                byte[] data = new byte[(int) length];
                bytesMessage.readBytes(data);
                messageText = new String(data, "UTF-8");
                logger.info("Converted BYTES to TEXT: " + messageText);
            } else {
                logger.warning("Empty BytesMessage received");
                return null;
            }
        } else {
            logger.warning("Received unsupported message type: " + message.getClass().getSimpleName());
            return null;
        }
        
        if (messageText == null || messageText.trim().isEmpty()) {
            logger.warning("No valid message content found");
            return null;
        }
        
        return messageText;
    }

    private void processPatientRegistration(PatientRegisterRequest request, Jsonb jsonb) throws Exception {
        logger.info("Processing patient registration for DNI: " + request.identityDocumentNumber);
        
        // Check if patient already exists with this DNI
        if (isPatientAlreadyRegistered(request.documentType, request.identityDocumentNumber)) {
            logger.warning("Patient with DNI " + request.identityDocumentNumber + " already exists. Registration aborted.");
            return;
        }
        
        // Create patient profile
        PatientProfile createdProfile = createPatientProfile(request);
        if (createdProfile != null) {
            // Send registration message to IAM
            RegisterAccountMessage iamMessage = new RegisterAccountMessage(
                "PATIENT", 
                request.password, 
                request.documentType, 
                request.identityDocumentNumber
            );
            sendToIAM(iamMessage, jsonb);
            logger.info("Patient registration completed for DNI: " + request.identityDocumentNumber);
        }
    }

    private void processTherapistRegistration(TherapistRegisterRequest request, Jsonb jsonb) throws Exception {
        logger.info("Processing therapist registration for DNI: " + request.identityDocumentNumber);
        
        // Check if therapist already exists with this DNI
        if (isTherapistAlreadyRegistered(request.documentType, request.identityDocumentNumber)) {
            logger.warning("Therapist with DNI " + request.identityDocumentNumber + " already exists. Registration aborted.");
            return;
        }
        
        // Create therapist profile
        TherapistProfile createdProfile = createTherapistProfile(request);
        if (createdProfile != null) {
            // Send registration message to IAM
            RegisterAccountMessage iamMessage = new RegisterAccountMessage(
                "THERAPIST", 
                request.password, 
                request.documentType, 
                request.identityDocumentNumber
            );
            sendToIAM(iamMessage, jsonb);
            logger.info("Therapist registration completed for DNI: " + request.identityDocumentNumber);
        }
    }

    private void processLegalResponsibleRegistration(LegalResponsibleRegisterRequest request, Jsonb jsonb) throws Exception {
        logger.info("Processing legal responsible registration for DNI: " + request.identityDocumentNumber);
        
        // Check if legal responsible already exists with this DNI
        if (isLegalResponsibleAlreadyRegistered(request.documentType, request.identityDocumentNumber)) {
            logger.warning("Legal Responsible with DNI " + request.identityDocumentNumber + " already exists. Registration aborted.");
            return;
        }
        
        // Create legal responsible profile
        LegalResponsibleProfile createdProfile = createLegalResponsibleProfile(request);
        if (createdProfile != null) {
            // Send registration message to IAM
            RegisterAccountMessage iamMessage = new RegisterAccountMessage(
                "LEGAL_RESPONSIBLE", 
                request.password, 
                request.documentType, 
                request.identityDocumentNumber
            );
            sendToIAM(iamMessage, jsonb);
            logger.info("Legal Responsible registration completed for DNI: " + request.identityDocumentNumber);
        }
    }

    private boolean isPatientAlreadyRegistered(String documentType, String documentNumber) {
        try {
            PatientProfileQueryService queryService = CDI.current().select(PatientProfileQueryService.class).get();
            GetPatientProfileByDocumentQuery query = new GetPatientProfileByDocumentQuery(documentType, documentNumber);
            Optional<PatientProfile> existing = queryService.handle(query);
            return existing.isPresent();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error checking if patient exists: " + e.getMessage(), e);
            return false;
        }
    }

    private boolean isTherapistAlreadyRegistered(String documentType, String documentNumber) {
        try {
            TherapistProfileQueryService queryService = CDI.current().select(TherapistProfileQueryService.class).get();
            GetTherapistProfileByDocumentQuery query = new GetTherapistProfileByDocumentQuery(documentType, documentNumber);
            Optional<TherapistProfile> existing = queryService.handle(query);
            return existing.isPresent();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error checking if therapist exists: " + e.getMessage(), e);
            return false;
        }
    }

    private boolean isLegalResponsibleAlreadyRegistered(String documentType, String documentNumber) {
        try {
            LegalResponsibleProfileQueryService queryService = CDI.current().select(LegalResponsibleProfileQueryService.class).get();
            GetLegalResponsibleProfileByDocumentQuery query = new GetLegalResponsibleProfileByDocumentQuery(documentType, documentNumber);
            Optional<LegalResponsibleProfile> existing = queryService.handle(query);
            return existing.isPresent();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error checking if legal responsible exists: " + e.getMessage(), e);
            return false;
        }
    }

    private PatientProfile createPatientProfile(PatientRegisterRequest request) {
        try {
            PatientProfileCommandService commandService = CDI.current().select(PatientProfileCommandService.class).get();
            
            CreatePatientProfileCommand command = new CreatePatientProfileCommand(
                request.firstNames, request.paternalSurname, request.maternalSurname,
                request.identityDocumentNumber, request.documentType, request.phone, request.email,
                request.birthPlace, request.birthDate, request.firstAppointmentAge, request.currentAge,
                request.gender, request.maritalStatus, request.currentAddress, request.district,
                request.province, request.region, request.country, request.religion,
                request.educationLevel, request.occupation, request.currentEducationalInstitution,
                request.referredTherapistName, 
                request.legalResponsibleId != null ? Integer.parseInt(request.legalResponsibleId) : null,
                request.therapistId != null ? Integer.parseInt(request.therapistId) : null
            );
            
            Optional<PatientProfile> result = commandService.handle(command);
            return result.orElse(null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating patient profile: " + e.getMessage(), e);
            return null;
        }
    }

    private TherapistProfile createTherapistProfile(TherapistRegisterRequest request) {
        try {
            TherapistProfileCommandService commandService = CDI.current().select(TherapistProfileCommandService.class).get();
            
            CreateTherapistProfileCommand command = new CreateTherapistProfileCommand(
                request.firstNames, request.paternalSurname, request.maternalSurname,
                request.identityDocumentNumber, request.documentType, request.phone, request.email,
                request.specialtyName, request.attentionPlaceAddress
            );
            
            Optional<TherapistProfile> result = commandService.handle(command);
            return result.orElse(null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating therapist profile: " + e.getMessage(), e);
            return null;
        }
    }

    private LegalResponsibleProfile createLegalResponsibleProfile(LegalResponsibleRegisterRequest request) {
        try {
            LegalResponsibleProfileCommandService commandService = CDI.current().select(LegalResponsibleProfileCommandService.class).get();
            
            CreateLegalResponsibleProfileCommand command = new CreateLegalResponsibleProfileCommand(
                request.firstNames, request.paternalSurname, request.maternalSurname,
                request.identityDocumentNumber, request.documentType, request.phone, 
                request.email, request.relationship
            );
            
            Optional<LegalResponsibleProfile> result = commandService.handle(command);
            return result.orElse(null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating legal responsible profile: " + e.getMessage(), e);
            return null;
        }
    }

    private void sendToIAM(RegisterAccountMessage message, Jsonb jsonb) throws Exception {
        String messageJson = jsonb.toJson(message);
        TextMessage iamMessage = session.createTextMessage(messageJson);
        producer.send(iamMessage);
        logger.info("Sent registration message to IAM: " + messageJson);
    }

    // DTO Classes for incoming registration requests
    public static class PatientRegisterRequest {
        public String password;
        public String firstNames;
        public String paternalSurname;
        public String maternalSurname;
        public String identityDocumentNumber;
        public String documentType;
        public String phone;
        public String email;
        public String birthPlace;
        public String birthDate;
        public Integer firstAppointmentAge;
        public Integer currentAge;
        public String gender;
        public String maritalStatus;
        public String currentAddress;
        public String district;
        public String province;
        public String region;
        public String country;
        public String religion;
        public String educationLevel;
        public String occupation;
        public String currentEducationalInstitution;
        public String referredTherapistName;
        public String legalResponsibleId;
        public String therapistId;
    }

    public static class TherapistRegisterRequest {
        public String password;
        public String firstNames;
        public String paternalSurname;
        public String maternalSurname;
        public String identityDocumentNumber;
        public String documentType;
        public String phone;
        public String email;
        public String specialtyName;
        public String attentionPlaceAddress;
    }

    public static class LegalResponsibleRegisterRequest {
        public String password;
        public String documentType;
        public String email;
        public String firstNames;
        public String identityDocumentNumber;
        public String maternalSurname;
        public String paternalSurname;
        public String phone;
        public String relationship;
    }

    // DTO for outgoing IAM registration message
    public static class RegisterAccountMessage {
        public String accountType;
        public String password;
        public String documentType;
        public String identityDocumentNumber;

        public RegisterAccountMessage(String accountType, String password, String documentType, String identityDocumentNumber) {
            this.accountType = accountType;
            this.password = password;
            this.documentType = documentType;
            this.identityDocumentNumber = identityDocumentNumber;
        }
    }
}