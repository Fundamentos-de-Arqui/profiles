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

import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileQueryService;

import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetPatientProfileByIdQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetTherapistProfileByIdQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetLegalResponsibleProfileByIdQuery;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;

@WebListener
public class SessionProfileActiveMQListener implements ServletContextListener, MessageListener {
    private static final Logger logger = Logger.getLogger(SessionProfileActiveMQListener.class.getName());
    private static Connection connection;
    private static Session session;
    private static MessageConsumer consumer;
    private static MessageProducer producer;
    private static boolean isInitialized = false;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        synchronized (SessionProfileActiveMQListener.class) {
            if (isInitialized) {
                logger.info("SessionProfileActiveMQListener already initialized, skipping...");
                return;
            }
            try {
                closeExistingConnections();
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                connection = connectionFactory.createConnection();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue inputQueue = session.createQueue("profile_getSessions");
                Queue outputQueue = session.createQueue("apigateway_getSessions");
                consumer = session.createConsumer(inputQueue);
                producer = session.createProducer(outputQueue);
                consumer.setMessageListener(this);
                connection.start();
                isInitialized = true;
                logger.info("SUCCESS: SessionProfileActiveMQListener connected to profile_getSessions and ready to send to apigateway_getSessions");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "FAILED to initialize SessionProfileActiveMQListener: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== SessionProfileActiveMQListener STOPPING ===");
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
            logger.info("=== MESSAGE RECEIVED BY SESSION PROFILE LISTENER ===");
            String messageText = extractMessageText(message);
            if (messageText == null) return;

            Jsonb jsonb = JsonbBuilder.create();
            SessionRequest request = jsonb.fromJson(messageText, SessionRequest.class);
            
            if (request == null || request.id == null) {
                logger.warning("Invalid session request: " + messageText);
                return;
            }

            // Get profile names by IDs
            String therapistName = getTherapistName(request.therapist_id);
            String patientName = getPatientName(request.patient_id);
            String legalResponsibleName = getLegalResponsibleName(request.legal_responsible_id);

            // Create response with names
            SessionResponse response = new SessionResponse(
                request.id,
                therapistName,
                patientName,
                legalResponsibleName,
                request.start_at,
                request.ends_at,
                request.status
            );

            // Send response
            String responseJson = jsonb.toJson(response);
            TextMessage responseMessage = session.createTextMessage(responseJson);
            producer.send(responseMessage);
            logger.info("Sent session profile data to apigateway_getSessions for session ID: " + request.id);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing session profile message: " + e.getMessage(), e);
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

    private String getTherapistName(Integer therapistId) {
        if (therapistId == null) {
            return null;
        }
        
        try {
            TherapistProfileQueryService queryService = CDI.current().select(TherapistProfileQueryService.class).get();
            GetTherapistProfileByIdQuery query = new GetTherapistProfileByIdQuery(therapistId);
            Optional<TherapistProfile> therapistOpt = queryService.handle(query);
            
            if (therapistOpt.isPresent()) {
                TherapistProfile therapist = therapistOpt.get();
                return buildFullName(
                    therapist.getIdentity().firstNames().value(),
                    therapist.getIdentity().paternalSurname().value(),
                    therapist.getIdentity().maternalSurname() != null ? therapist.getIdentity().maternalSurname().value() : null
                );
            } else {
                logger.warning("Therapist not found for ID: " + therapistId);
                return "Unknown Therapist";
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting therapist name for ID " + therapistId + ": " + e.getMessage(), e);
            return "Error Loading Therapist";
        }
    }

    private String getPatientName(Integer patientId) {
        if (patientId == null) {
            return null;
        }
        
        try {
            PatientProfileQueryService queryService = CDI.current().select(PatientProfileQueryService.class).get();
            GetPatientProfileByIdQuery query = new GetPatientProfileByIdQuery(patientId);
            Optional<PatientProfile> patientOpt = queryService.handle(query);
            
            if (patientOpt.isPresent()) {
                PatientProfile patient = patientOpt.get();
                return buildFullName(
                    patient.getIdentity().firstNames().value(),
                    patient.getIdentity().paternalSurname().value(),
                    patient.getIdentity().maternalSurname() != null ? patient.getIdentity().maternalSurname().value() : null
                );
            } else {
                logger.warning("Patient not found for ID: " + patientId);
                return "Unknown Patient";
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting patient name for ID " + patientId + ": " + e.getMessage(), e);
            return "Error Loading Patient";
        }
    }

    private String getLegalResponsibleName(Integer legalResponsibleId) {
        if (legalResponsibleId == null) {
            return null;
        }
        
        try {
            LegalResponsibleProfileQueryService queryService = CDI.current().select(LegalResponsibleProfileQueryService.class).get();
            GetLegalResponsibleProfileByIdQuery query = new GetLegalResponsibleProfileByIdQuery(legalResponsibleId);
            Optional<LegalResponsibleProfile> legalResponsibleOpt = queryService.handle(query);
            
            if (legalResponsibleOpt.isPresent()) {
                LegalResponsibleProfile legalResponsible = legalResponsibleOpt.get();
                return buildFullName(
                    legalResponsible.getIdentity().firstNames().value(),
                    legalResponsible.getIdentity().paternalSurname().value(),
                    legalResponsible.getIdentity().maternalSurname() != null ? legalResponsible.getIdentity().maternalSurname().value() : null
                );
            } else {
                logger.warning("Legal Responsible not found for ID: " + legalResponsibleId);
                return "Unknown Legal Responsible";
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting legal responsible name for ID " + legalResponsibleId + ": " + e.getMessage(), e);
            return "Error Loading Legal Responsible";
        }
    }

    private String buildFullName(String firstNames, String paternalSurname, String maternalSurname) {
        StringBuilder fullName = new StringBuilder();
        fullName.append(firstNames).append(" ").append(paternalSurname);
        if (maternalSurname != null && !maternalSurname.trim().isEmpty()) {
            fullName.append(" ").append(maternalSurname);
        }
        return fullName.toString();
    }

    // DTO for incoming session request
    public static class SessionRequest {
        public Integer id;
        public Integer therapist_id;
        public Integer patient_id;
        public Integer legal_responsible_id;
        public String start_at;
        public String ends_at;
        public String status;
    }

    // DTO for outgoing session response with names
    public static class SessionResponse {
        public Integer id;
        public String therapist_name;
        public String patient_name;
        public String legal_responsible_name;
        public String start_at;
        public String ends_at;
        public String status;

        public SessionResponse(Integer id, String therapist_name, String patient_name, 
                              String legal_responsible_name, String start_at, String ends_at, String status) {
            this.id = id;
            this.therapist_name = therapist_name;
            this.patient_name = patient_name;
            this.legal_responsible_name = legal_responsible_name;
            this.start_at = start_at;
            this.ends_at = ends_at;
            this.status = status;
        }
    }
}