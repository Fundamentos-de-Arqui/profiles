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

import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetAllLegalResponsibleProfilesQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;

@WebListener
public class LegalResponsibleProfilesActiveMQListener implements ServletContextListener, MessageListener {
    private static final Logger logger = Logger.getLogger(LegalResponsibleProfilesActiveMQListener.class.getName());
    private static Connection connection;
    private static Session session;
    private static MessageConsumer consumer;
    private static MessageProducer producer;
    private static boolean isInitialized = false;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        synchronized (LegalResponsibleProfilesActiveMQListener.class) {
            if (isInitialized) {
                logger.info("LegalResponsibleProfilesActiveMQListener already initialized, skipping...");
                return;
            }
            try {
                closeExistingConnections();
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                connection = connectionFactory.createConnection();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue inputQueue = session.createQueue("profiles_legal-responsibleProfiles");
                Queue outputQueue = session.createQueue("apigateway_legal-responsibleProfiles");
                consumer = session.createConsumer(inputQueue);
                producer = session.createProducer(outputQueue);
                consumer.setMessageListener(this);
                connection.start();
                isInitialized = true;
                logger.info("SUCCESS: LegalResponsibleProfilesActiveMQListener connected to profiles_legal-responsibleProfiles and ready to send to apigateway_legal-responsibleProfiles");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "FAILED to initialize LegalResponsibleProfilesActiveMQListener: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== LegalResponsibleProfilesActiveMQListener STOPPING ===");
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
            logger.info("=== MESSAGE RECEIVED BY LEGAL RESPONSIBLE PROFILES LISTENER ===");
            String messageText = extractMessageText(message);
            if (messageText == null) return;

            // Get all legal responsible profiles
            LegalResponsibleProfileQueryService queryService = CDI.current().select(LegalResponsibleProfileQueryService.class).get();
            GetAllLegalResponsibleProfilesQuery query = new GetAllLegalResponsibleProfilesQuery();
            List<LegalResponsibleProfile> legalResponsibles = queryService.handle(query);

            // Transform to DTOs
            List<LegalResponsibleDto> legalResponsibleDtos = legalResponsibles.stream()
                .map(this::mapToDto)
                .toList();

            // Send response
            Jsonb jsonb = JsonbBuilder.create();
            String responseJson = jsonb.toJson(legalResponsibleDtos);
            TextMessage responseMessage = session.createTextMessage(responseJson);
            producer.send(responseMessage);
            logger.info("Sent " + legalResponsibleDtos.size() + " legal responsible profiles to apigateway_legal-responsibleProfiles");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing legal responsible profiles message: " + e.getMessage(), e);
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

    private LegalResponsibleDto mapToDto(LegalResponsibleProfile legalResponsible) {
        return new LegalResponsibleDto(
            legalResponsible.getId().value(),
            legalResponsible.getIdentity().firstNames().value(),
            legalResponsible.getIdentity().paternalSurname().value(),
            legalResponsible.getIdentity().maternalSurname() != null ? legalResponsible.getIdentity().maternalSurname().value() : null,
            legalResponsible.getIdentity().identityDocumentNumber().value(),
            legalResponsible.getIdentity().documentType().value(),
            legalResponsible.getIdentity().phone().value(),
            legalResponsible.getIdentity().email().value(),
            legalResponsible.getRelationship().description()
        );
    }

    // DTO for legal responsible response
    public static class LegalResponsibleDto {
        public Integer id;
        public String firstNames;
        public String paternalSurname;
        public String maternalSurname;
        public String identityDocumentNumber;
        public String documentType;
        public String phone;
        public String email;
        public String relationship;

        public LegalResponsibleDto(Integer id, String firstNames, String paternalSurname, String maternalSurname,
                                  String identityDocumentNumber, String documentType, String phone, 
                                  String email, String relationship) {
            this.id = id;
            this.firstNames = firstNames;
            this.paternalSurname = paternalSurname;
            this.maternalSurname = maternalSurname;
            this.identityDocumentNumber = identityDocumentNumber;
            this.documentType = documentType;
            this.phone = phone;
            this.email = email;
            this.relationship = relationship;
        }
    }
}