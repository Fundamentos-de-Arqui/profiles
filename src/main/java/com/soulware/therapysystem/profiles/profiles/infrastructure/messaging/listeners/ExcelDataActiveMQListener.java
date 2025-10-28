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
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetPatientProfileWithRelationsByDocumentQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.PatientProfileWithRelations;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.PatientProfileWithRelationsResource;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform.PatientProfileWithRelationsResourceFromEntityAssembler;

@WebListener
public class ExcelDataActiveMQListener implements ServletContextListener, MessageListener {
    private static final Logger logger = Logger.getLogger(ExcelDataActiveMQListener.class.getName());
    private static Connection connection;
    private static Session session;
    private static MessageConsumer consumer;
    private static MessageProducer producer;
    private static boolean isInitialized = false;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        synchronized (ExcelDataActiveMQListener.class) {
            if (isInitialized) {
                logger.info("ExcelDataActiveMQListener already initialized, skipping...");
                return;
            }
            try {
                closeExistingConnections();
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                connection = connectionFactory.createConnection();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue inputQueue = session.createQueue("profiles_getExcelData");
                Queue outputQueue = session.createQueue("excelParser_patientForm");
                consumer = session.createConsumer(inputQueue);
                producer = session.createProducer(outputQueue);
                consumer.setMessageListener(this);
                connection.start();
                isInitialized = true;
                logger.info("SUCCESS: ExcelDataActiveMQListener connected to profiles_getExcelData and ready to send to excelParser_patientForm");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "FAILED to initialize ExcelDataActiveMQListener: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== ExcelDataActiveMQListener STOPPING ===");
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
            logger.info("=== MESSAGE RECEIVED BY EXCEL DATA LISTENER ===");
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
            ExcelDataRequest request = jsonb.fromJson(messageText, ExcelDataRequest.class);
            if (request == null || request.type == null || request.documentNumber == null) {
                logger.warning("Invalid ExcelDataRequest message: " + messageText);
                return;
            }
            // Buscar paciente y relaciones
            PatientProfileQueryService queryService = CDI.current().select(PatientProfileQueryService.class).get();
            GetPatientProfileWithRelationsByDocumentQuery query = new GetPatientProfileWithRelationsByDocumentQuery(request.type, request.documentNumber);
            PatientProfileWithRelations profileWithRelations = queryService.handle(query).orElse(null);
            if (profileWithRelations == null) {
                logger.warning("No patient found for document: " + request.type + " - " + request.documentNumber);
                return;
            }
            PatientProfileWithRelationsResource resource = PatientProfileWithRelationsResourceFromEntityAssembler.toResourceFromEntity(profileWithRelations);
            String responseJson = jsonb.toJson(resource);
            TextMessage responseMessage = session.createTextMessage(responseJson);
            producer.send(responseMessage);
            logger.info("Sent patient data to excelParser_patientForm for document: " + request.type + " - " + request.documentNumber);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing ExcelData message: " + e.getMessage(), e);
        }
    }

    // DTO para deserializar el mensaje recibido
    public static class ExcelDataRequest {
        public String type;
        public String documentNumber;
        public String timestamp;
    }
}
