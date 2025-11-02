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

import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetPatientProfileByIdQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetTherapistProfileByIdQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;

@WebListener
public class MedicalRecordActiveMQListener implements ServletContextListener, MessageListener {
    private static final Logger logger = Logger.getLogger(MedicalRecordActiveMQListener.class.getName());
    private static Connection connection;
    private static Session session;
    private static MessageConsumer consumer;
    private static MessageProducer producer;
    private static boolean isInitialized = false;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        synchronized (MedicalRecordActiveMQListener.class) {
            if (isInitialized) {
                logger.info("MedicalRecordActiveMQListener already initialized, skipping...");
                return;
            }
            try {
                closeExistingConnections();
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                connection = connectionFactory.createConnection();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue inputQueue = session.createQueue("profiles_getMedicalRecord");
                Queue outputQueue = session.createQueue("apigateway_filiationFiles");
                consumer = session.createConsumer(inputQueue);
                producer = session.createProducer(outputQueue);
                consumer.setMessageListener(this);
                connection.start();
                isInitialized = true;
                logger.info("SUCCESS: MedicalRecordActiveMQListener connected to profiles_getMedicalRecord and ready to send to apigateway_filiationFiles");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "FAILED to initialize MedicalRecordActiveMQListener: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== MedicalRecordActiveMQListener STOPPING ===");
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
            logger.info("=== MESSAGE RECEIVED BY MEDICAL RECORD LISTENER ===");
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
                    return;
                }
            } else {
                logger.warning("Received unsupported message type: " + message.getClass().getSimpleName());
                return;
            }
            if (messageText == null || messageText.trim().isEmpty()) {
                logger.warning("No valid message content found");
                return;
            }

            Jsonb jsonb = JsonbBuilder.create();
            MedicalRecordRequest request = jsonb.fromJson(messageText, MedicalRecordRequest.class);
            
            if (request == null || request.patientId == null || request.therapistId == null) {
                logger.warning("Invalid medical record request: " + messageText);
                return;
            }

            // Obtener servicios
            PatientProfileQueryService patientQueryService = CDI.current().select(PatientProfileQueryService.class).get();
            TherapistProfileQueryService therapistQueryService = CDI.current().select(TherapistProfileQueryService.class).get();

            // Buscar paciente
            GetPatientProfileByIdQuery patientQuery = new GetPatientProfileByIdQuery(request.patientId);
            PatientProfile patient = patientQueryService.handle(patientQuery).orElse(null);
            
            if (patient == null) {
                logger.warning("Patient not found for ID: " + request.patientId);
                return;
            }

            // Buscar terapeuta
            GetTherapistProfileByIdQuery therapistQuery = new GetTherapistProfileByIdQuery(request.therapistId);
            TherapistProfile therapist = therapistQueryService.handle(therapistQuery).orElse(null);
            
            if (therapist == null) {
                logger.warning("Therapist not found for ID: " + request.therapistId);
                return;
            }

            // Crear DTOs de respuesta
            PatientDto patientDto = new PatientDto(
                patient.getBirthData().birthDate().toString(),
                patient.getBirthData().birthPlace(),
                patient.getAddress().country(),
                patient.getAddress().currentAddress(),
                patient.getAge().current(),
                patient.getEducationData().currentEducationalInstitution(),
                patient.getAddress().district(),
                patient.getIdentity().documentType().value(),
                patient.getEducationData().educationLevel(),
                patient.getIdentity().email().value(),
                patient.getAge().firstAppointment(),
                patient.getIdentity().firstNames().value(),
                patient.getGender().value(),
                patient.getIdentity().identityDocumentNumber().value(),
                patient.getMaritalStatus().value(),
                patient.getIdentity().maternalSurname() != null ? patient.getIdentity().maternalSurname().value() : null,
                patient.getEducationData().occupation(),
                patient.getIdentity().paternalSurname().value(),
                patient.getIdentity().phone().value(),
                patient.getAddress().province(),
                patient.getAddress().region(),
                patient.getReligion().value()
            );

            TherapistDto therapistDto = new TherapistDto(
                therapist.getAttentionPlace().address(),
                therapist.getIdentity().documentType().value(),
                therapist.getIdentity().email().value(),
                therapist.getIdentity().firstNames().value(),
                therapist.getIdentity().identityDocumentNumber().value(),
                therapist.getIdentity().maternalSurname() != null ? therapist.getIdentity().maternalSurname().value() : null,
                therapist.getIdentity().paternalSurname().value(),
                therapist.getIdentity().phone().value(),
                therapist.getSpecialty().name()
            );

            // Crear respuesta completa
            MedicalRecordResponse response = new MedicalRecordResponse(
                request.id,
                request.scheduledAt,
                request.createdAt,
                request.assessmentType,
                request.description,
                request.diagnostic,
                request.treatment,
                request.versionNumber,
                patientDto,
                therapistDto
            );

            // Enviar respuesta
            String responseJson = jsonb.toJson(response);
            TextMessage responseMessage = session.createTextMessage(responseJson);
            producer.send(responseMessage);
            logger.info("Sent medical record data to apigateway_filiationFiles for patient ID: " + request.patientId + " and therapist ID: " + request.therapistId);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing medical record message: " + e.getMessage(), e);
        }
    }

    // DTO para deserializar la solicitud de expediente médico
    public static class MedicalRecordRequest {
        public Integer id;
        public Integer versionNumber;
        public String diagnostic;
        public String treatment;
        public String description;
        public Integer patientId;
        public Integer therapistId;
        public String assessmentType;
        public String scheduledAt;
        public String createdAt;
    }

    // DTO para datos del paciente en la respuesta
    public static class PatientDto {
        public String birthDate;
        public String birthPlace;
        public String country;
        public String currentAddress;
        public Integer currentAge;
        public String currentEducationalInstitution;
        public String district;
        public String documentType;
        public String educationLevel;
        public String email;
        public Integer firstAppointmentAge;
        public String firstNames;
        public String gender;
        public String identityDocumentNumber;
        public String maritalStatus;
        public String maternalSurname;
        public String occupation;
        public String paternalSurname;
        public String phone;
        public String province;
        public String region;
        public String religion;

        public PatientDto(String birthDate, String birthPlace, String country, String currentAddress, 
                         Integer currentAge, String currentEducationalInstitution, String district, 
                         String documentType, String educationLevel, String email, Integer firstAppointmentAge,
                         String firstNames, String gender, String identityDocumentNumber, String maritalStatus,
                         String maternalSurname, String occupation, String paternalSurname, String phone,
                         String province, String region, String religion) {
            this.birthDate = birthDate;
            this.birthPlace = birthPlace;
            this.country = country;
            this.currentAddress = currentAddress;
            this.currentAge = currentAge;
            this.currentEducationalInstitution = currentEducationalInstitution;
            this.district = district;
            this.documentType = documentType;
            this.educationLevel = educationLevel;
            this.email = email;
            this.firstAppointmentAge = firstAppointmentAge;
            this.firstNames = firstNames;
            this.gender = gender;
            this.identityDocumentNumber = identityDocumentNumber;
            this.maritalStatus = maritalStatus;
            this.maternalSurname = maternalSurname;
            this.occupation = occupation;
            this.paternalSurname = paternalSurname;
            this.phone = phone;
            this.province = province;
            this.region = region;
            this.religion = religion;
        }
    }

    // DTO para datos del terapeuta en la respuesta
    public static class TherapistDto {
        public String attentionPlaceAddress;
        public String documentType;
        public String email;
        public String firstNames;
        public String identityDocumentNumber;
        public String maternalSurname;
        public String paternalSurname;
        public String phone;
        public String specialtyName;

        public TherapistDto(String attentionPlaceAddress, String documentType, String email, String firstNames,
                           String identityDocumentNumber, String maternalSurname, String paternalSurname,
                           String phone, String specialtyName) {
            this.attentionPlaceAddress = attentionPlaceAddress;
            this.documentType = documentType;
            this.email = email;
            this.firstNames = firstNames;
            this.identityDocumentNumber = identityDocumentNumber;
            this.maternalSurname = maternalSurname;
            this.paternalSurname = paternalSurname;
            this.phone = phone;
            this.specialtyName = specialtyName;
        }
    }

    // DTO para la respuesta completa del expediente médico
    public static class MedicalRecordResponse {
        public Integer id;
        public String scheduledAt;
        public String createdAt;
        public String assessmentType;
        public String description;
        public String diagnostic;
        public String treatment;
        public Integer versionNumber;
        public PatientDto patient;
        public TherapistDto therapist;

        public MedicalRecordResponse(Integer id, String scheduledAt, String createdAt, String assessmentType,
                                   String description, String diagnostic, String treatment, Integer versionNumber,
                                   PatientDto patient, TherapistDto therapist) {
            this.id = id;
            this.scheduledAt = scheduledAt;
            this.createdAt = createdAt;
            this.assessmentType = assessmentType;
            this.description = description;
            this.diagnostic = diagnostic;
            this.treatment = treatment;
            this.versionNumber = versionNumber;
            this.patient = patient;
            this.therapist = therapist;
        }
    }
}