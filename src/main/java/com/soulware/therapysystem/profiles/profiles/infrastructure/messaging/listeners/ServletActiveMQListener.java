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

import com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto.PatientProcessingMessage;
import com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.services.PatientMessageProcessingService;

/**
 * Alternative ActiveMQ listener using ServletContextListener
 * This will definitely start when the application deploys
 */
@WebListener
public class ServletActiveMQListener implements ServletContextListener, MessageListener {
    
    private static final Logger logger = Logger.getLogger(ServletActiveMQListener.class.getName());
    
    // Static para evitar múltiples instancias
    private static Connection connection;
    private static Session session;
    private static MessageConsumer consumer;
    private static boolean isInitialized = false;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Evitar inicialización múltiple
        synchronized (ServletActiveMQListener.class) {
            if (isInitialized) {
                System.out.println("ServletActiveMQListener already initialized, skipping...");
                logger.info("ServletActiveMQListener already initialized, skipping...");
                return;
            }
            
            System.out.println("=== ServletActiveMQListener STARTING ===");
            logger.info("=== ServletActiveMQListener STARTING ===");
            
            try {
                // Cerrar conexiones existentes si las hay
                closeExistingConnections();
                
                // Create connection factory
                logger.info("Creating ActiveMQ connection factory...");
                System.out.println("Creating ActiveMQ connection factory...");
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                
                // Create connection
                logger.info("Creating connection...");
                System.out.println("Creating connection...");
                connection = connectionFactory.createConnection();
                
                // Create session
                logger.info("Creating session...");
                System.out.println("Creating session...");
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                
                // Create queue
                logger.info("Creating queue: patient.processing.queue");
                System.out.println("Creating queue: patient.processing.queue");
                Queue queue = session.createQueue("patient.processing.queue");
                
                // Create consumer
                logger.info("Creating consumer...");
                System.out.println("Creating consumer...");
                consumer = session.createConsumer(queue);
                consumer.setMessageListener(this);
                
                // Start connection
                logger.info("Starting connection...");
                System.out.println("Starting connection...");
                connection.start();
                
                isInitialized = true;
                
                System.out.println("SUCCESS: ServletActiveMQListener connected to patient.processing.queue");
                logger.info("SUCCESS: ServletActiveMQListener connected to patient.processing.queue");
                
            } catch (Exception e) {
                System.err.println("FAILED to initialize ServletActiveMQListener: " + e.getMessage());
                logger.log(Level.SEVERE, "FAILED to initialize ServletActiveMQListener: " + e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== ServletActiveMQListener STOPPING ===");
        logger.info("=== ServletActiveMQListener STOPPING ===");
        
        closeExistingConnections();
    }
    
    private static void closeExistingConnections() {
        try {
            if (consumer != null) {
                consumer.close();
                consumer = null;
            }
            if (session != null) {
                session.close();
                session = null;
            }
            if (connection != null) {
                connection.close();
                connection = null;
            }
            isInitialized = false;
            System.out.println("Connections closed successfully");
            logger.info("Connections closed successfully");
        } catch (Exception e) {
            System.err.println("Error closing connections: " + e.getMessage());
            logger.log(Level.WARNING, "Error closing connections: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            System.out.println("=== MESSAGE RECEIVED BY SERVLET LISTENER ===");
            logger.info("=== MESSAGE RECEIVED BY SERVLET LISTENER ===");
            
            if (message instanceof TextMessage textMessage) {
                String messageText = textMessage.getText();
                System.out.println("Message type: TextMessage");
                System.out.println("Message content: " + messageText);
                logger.info("Received TEXT message: " + messageText);
                
                // Parse JSON message
                PatientProcessingMessage patientMessage = parseJsonMessage(messageText);
                if (patientMessage != null) {
                    // Process the patient data
                    processPatientMessage(patientMessage);
                    System.out.println("Patient data processed and saved to database successfully");
                    logger.info("Patient data processed and saved to database successfully");
                } else {
                    System.err.println("Failed to parse JSON message");
                    logger.severe("Failed to parse JSON message");
                }
                
            } else {
                System.out.println("Received non-text message: " + message.getClass().getSimpleName());
                logger.warning("Received non-text message: " + message.getClass().getSimpleName());
            }
            
            System.out.println("=== MESSAGE PROCESSING COMPLETE ===");
            logger.info("=== MESSAGE PROCESSING COMPLETE ===");
            
        } catch (Exception e) {
            System.err.println("ERROR processing message: " + e.getMessage());
            logger.log(Level.SEVERE, "ERROR processing message: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
    
    /**
     * Parse JSON message into PatientProcessingMessage object
     */
    private PatientProcessingMessage parseJsonMessage(String jsonMessage) {
        try {
            System.out.println("Parsing JSON message...");
            logger.info("Parsing JSON message...");
            
            try (Jsonb jsonb = JsonbBuilder.create()) {
                PatientProcessingMessage message = jsonb.fromJson(jsonMessage, PatientProcessingMessage.class);
                System.out.println("Successfully parsed message with ID: " + message.messageId());
                logger.info("Successfully parsed message with ID: " + message.messageId());
                return message;
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON message: " + e.getMessage());
            logger.log(Level.SEVERE, "Error parsing JSON message: " + e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Process the patient message using the service
     */
    private void processPatientMessage(PatientProcessingMessage message) {
        try {
            System.out.println("Processing patient message for: " + message.patientData().firstNames() + " " + message.patientData().paternalSurname());
            logger.info("Processing patient message for: " + message.patientData().firstNames() + " " + message.patientData().paternalSurname());
            
            // Get the service using CDI programmatic lookup since @Inject doesn't work in ServletContextListener
            PatientMessageProcessingService processingService = CDI.current().select(PatientMessageProcessingService.class).get();
            
            // Process only the patient data for now
            processingService.processPatientMessage(message.patientData());
            
            System.out.println("Patient profile created successfully in database");
            logger.info("Patient profile created successfully in database");
            
        } catch (Exception e) {
            System.err.println("Error processing patient message: " + e.getMessage());
            logger.log(Level.SEVERE, "Error processing patient message: " + e.getMessage(), e);
            e.printStackTrace();
            throw e; // Re-throw to trigger message retry if needed
        }
    }
}