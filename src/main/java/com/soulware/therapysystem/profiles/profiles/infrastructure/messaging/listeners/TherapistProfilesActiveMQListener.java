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
import java.util.List;

import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetAllTherapistProfilesQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;

@WebListener
public class TherapistProfilesActiveMQListener implements ServletContextListener, MessageListener {
    private static final Logger logger = Logger.getLogger(TherapistProfilesActiveMQListener.class.getName());
    private static Connection connection;
    private static Session session;
    private static MessageConsumer consumer;
    private static MessageProducer producer;
    private static boolean isInitialized = false;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        synchronized (TherapistProfilesActiveMQListener.class) {
            if (isInitialized) {
                logger.info("TherapistProfilesActiveMQListener already initialized, skipping...");
                return;
            }
            try {
                closeExistingConnections();
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                connection = connectionFactory.createConnection();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue inputQueue = session.createQueue("profiles_therapistProfiles");
                Queue outputQueue = session.createQueue("apigateway_therapistProfiles");
                consumer = session.createConsumer(inputQueue);
                producer = session.createProducer(outputQueue);
                consumer.setMessageListener(this);
                connection.start();
                isInitialized = true;
                logger.info("SUCCESS: TherapistProfilesActiveMQListener connected to profiles_therapistProfiles and ready to send to apigateway_therapistProfiles");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "FAILED to initialize TherapistProfilesActiveMQListener: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== TherapistProfilesActiveMQListener STOPPING ===");
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
            logger.info("=== MESSAGE RECEIVED BY THERAPIST PROFILES LISTENER ===");
            String messageText = extractMessageText(message);
            if (messageText == null) return;

            // Get all therapist profiles
            TherapistProfileQueryService queryService = CDI.current().select(TherapistProfileQueryService.class).get();
            GetAllTherapistProfilesQuery query = new GetAllTherapistProfilesQuery();
            List<TherapistProfile> therapists = queryService.handle(query);

            // Transform to DTOs
            List<TherapistDto> therapistDtos = therapists.stream()
                .map(this::mapToDto)
                .toList();

            // Send response
            Jsonb jsonb = JsonbBuilder.create();
            String responseJson = jsonb.toJson(therapistDtos);
            TextMessage responseMessage = session.createTextMessage(responseJson);
            producer.send(responseMessage);
            logger.info("Sent " + therapistDtos.size() + " therapist profiles to apigateway_therapistProfiles");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing therapist profiles message: " + e.getMessage(), e);
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

    private TherapistDto mapToDto(TherapistProfile therapist) {
        return new TherapistDto(
            therapist.getId().value(),
            therapist.getIdentity().firstNames().value(),
            therapist.getIdentity().paternalSurname().value(),
            therapist.getIdentity().maternalSurname() != null ? therapist.getIdentity().maternalSurname().value() : null,
            therapist.getIdentity().identityDocumentNumber().value(),
            therapist.getIdentity().documentType().value(),
            therapist.getIdentity().phone().value(),
            therapist.getIdentity().email().value(),
            therapist.getSpecialty().name(),
            therapist.getAttentionPlace().address()
        );
    }

    // DTO for therapist response
    public static class TherapistDto {
        public Integer id;
        public String firstNames;
        public String paternalSurname;
        public String maternalSurname;
        public String identityDocumentNumber;
        public String documentType;
        public String phone;
        public String email;
        public String specialtyName;
        public String attentionPlaceAddress;

        public TherapistDto(Integer id, String firstNames, String paternalSurname, String maternalSurname,
                           String identityDocumentNumber, String documentType, String phone, String email,
                           String specialtyName, String attentionPlaceAddress) {
            this.id = id;
            this.firstNames = firstNames;
            this.paternalSurname = paternalSurname;
            this.maternalSurname = maternalSurname;
            this.identityDocumentNumber = identityDocumentNumber;
            this.documentType = documentType;
            this.phone = phone;
            this.email = email;
            this.specialtyName = specialtyName;
            this.attentionPlaceAddress = attentionPlaceAddress;
        }
    }
}