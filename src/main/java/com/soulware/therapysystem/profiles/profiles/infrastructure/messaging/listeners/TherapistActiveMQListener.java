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

import com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto.TherapistProcessingMessage;
import com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.dto.SimpleTherapistData;
import com.soulware.therapysystem.profiles.profiles.infrastructure.messaging.services.TherapistMessageProcessingService;

/**
 * ActiveMQ listener for therapist profiles using ServletContextListener
 * Listens to the 'profiles_therapist' queue for therapist data
 */
@WebListener
public class TherapistActiveMQListener implements ServletContextListener, MessageListener {
    
    private static final Logger logger = Logger.getLogger(TherapistActiveMQListener.class.getName());
    
    // Static para evitar múltiples instancias
    private static Connection connection;
    private static Session session;
    private static MessageConsumer consumer;
    private static boolean isInitialized = false;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Evitar inicialización múltiple
        synchronized (TherapistActiveMQListener.class) {
            if (isInitialized) {
                System.out.println("TherapistActiveMQListener already initialized, skipping...");
                logger.info("TherapistActiveMQListener already initialized, skipping...");
                return;
            }
            
            System.out.println("=== TherapistActiveMQListener STARTING ===");
            logger.info("=== TherapistActiveMQListener STARTING ===");
            
            try {
                // Cerrar conexiones existentes si las hay
                closeExistingConnections();
                
                // Create connection factory
                logger.info("Creating ActiveMQ connection factory for therapist queue...");
                System.out.println("Creating ActiveMQ connection factory for therapist queue...");
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                
                // Create connection
                logger.info("Creating connection...");
                System.out.println("Creating connection...");
                connection = connectionFactory.createConnection();
                
                // Create session
                logger.info("Creating session...");
                System.out.println("Creating session...");
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                
                // Create queue for therapist profiles
                logger.info("Creating queue: profiles_therapist");
                System.out.println("Creating queue: profiles_therapist");
                Queue queue = session.createQueue("profiles_therapist");
                
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
                
                System.out.println("SUCCESS: TherapistActiveMQListener connected to profiles_therapist queue");
                logger.info("SUCCESS: TherapistActiveMQListener connected to profiles_therapist queue");
                
            } catch (Exception e) {
                System.err.println("FAILED to initialize TherapistActiveMQListener: " + e.getMessage());
                logger.log(Level.SEVERE, "FAILED to initialize TherapistActiveMQListener: " + e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== TherapistActiveMQListener STOPPING ===");
        logger.info("=== TherapistActiveMQListener STOPPING ===");
        
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
            System.out.println("Therapist listener connections closed successfully");
            logger.info("Therapist listener connections closed successfully");
        } catch (Exception e) {
            System.err.println("Error closing therapist listener connections: " + e.getMessage());
            logger.log(Level.WARNING, "Error closing therapist listener connections: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            System.out.println("=== THERAPIST MESSAGE RECEIVED ===");
            logger.info("=== THERAPIST MESSAGE RECEIVED ===");
            
            String messageText = null;
            
            if (message instanceof TextMessage textMessage) {
                messageText = textMessage.getText();
                System.out.println("Message type: TextMessage");
                System.out.println("Message content: " + messageText);
                logger.info("Received THERAPIST TEXT message: " + messageText);
                
            } else if (message instanceof BytesMessage bytesMessage) {
                // Handle BytesMessage
                System.out.println("Message type: BytesMessage");
                logger.info("Received THERAPIST BYTES message");
                
                // Get the length of the message
                long length = bytesMessage.getBodyLength();
                if (length > 0) {
                    byte[] data = new byte[(int) length];
                    bytesMessage.readBytes(data);
                    messageText = new String(data, "UTF-8");
                    System.out.println("Message content: " + messageText);
                    logger.info("Converted BYTES to TEXT: " + messageText);
                } else {
                    System.err.println("Empty BytesMessage received");
                    logger.severe("Empty BytesMessage received");
                }
                
            } else {
                System.out.println("Received unsupported therapist message type: " + message.getClass().getSimpleName());
                logger.warning("Received unsupported therapist message type: " + message.getClass().getSimpleName());
            }
            
            // Process the message if we have text content
            if (messageText != null && !messageText.trim().isEmpty()) {
                // Try to parse as complete message first, then as simple therapist data
                TherapistProcessingMessage therapistMessage = parseJsonMessage(messageText);
                SimpleTherapistData simpleTherapistData = null;
                
                if (therapistMessage != null) {
                    // Process the complete therapist message
                    processTherapistMessage(therapistMessage);
                    System.out.println("Therapist data processed and saved to database successfully");
                    logger.info("Therapist data processed and saved to database successfully");
                } else {
                    // Try to parse as simple therapist data (direct format)
                    System.out.println("Attempting to parse as SimpleTherapistData...");
                    simpleTherapistData = parseSimpleTherapistData(messageText);
                    if (simpleTherapistData != null) {
                        System.out.println("Successfully parsed SimpleTherapistData: " + simpleTherapistData);
                        processSimpleTherapistData(simpleTherapistData);
                        System.out.println("Simple therapist data processed and saved to database successfully");
                        logger.info("Simple therapist data processed and saved to database successfully");
                    } else {
                        System.err.println("Failed to parse therapist JSON message in any format.");
                        System.err.println("Message content was: " + messageText);
                        System.err.println("Message length: " + messageText.length());
                        logger.severe("Failed to parse therapist JSON message in any format: " + messageText);
                    }
                }
            } else {
                System.err.println("No valid message content found");
                logger.severe("No valid message content found");
            }
            
            System.out.println("=== THERAPIST MESSAGE PROCESSING COMPLETE ===");
            logger.info("=== THERAPIST MESSAGE PROCESSING COMPLETE ===");
            
        } catch (Exception e) {
            System.err.println("ERROR processing therapist message: " + e.getMessage());
            logger.log(Level.SEVERE, "ERROR processing therapist message: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
    
    /**
     * Parse JSON message into TherapistProcessingMessage object
     */
    private TherapistProcessingMessage parseJsonMessage(String jsonMessage) {
        try {
            System.out.println("Parsing therapist JSON message...");
            logger.info("Parsing therapist JSON message...");
            
            try (Jsonb jsonb = JsonbBuilder.create()) {
                TherapistProcessingMessage message = jsonb.fromJson(jsonMessage, TherapistProcessingMessage.class);
                System.out.println("Successfully parsed therapist message with ID: " + message.messageId());
                logger.info("Successfully parsed therapist message with ID: " + message.messageId());
                return message;
            }
        } catch (Exception e) {
            System.err.println("Error parsing therapist JSON message: " + e.getMessage());
            logger.log(Level.SEVERE, "Error parsing therapist JSON message: " + e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Process the therapist message using the service
     */
    private void processTherapistMessage(TherapistProcessingMessage message) {
        try {
            System.out.println("Processing therapist message for: " + message.therapistData().firstNames() + " " + message.therapistData().paternalSurname());
            logger.info("Processing therapist message for: " + message.therapistData().firstNames() + " " + message.therapistData().paternalSurname());
            
            // Get the service using CDI programmatic lookup since @Inject doesn't work in ServletContextListener
            TherapistMessageProcessingService processingService = CDI.current().select(TherapistMessageProcessingService.class).get();
            
            // Process the therapist data
            processingService.processTherapistMessage(message);
            
            System.out.println("Therapist profile created successfully in database");
            logger.info("Therapist profile created successfully in database");
            
        } catch (Exception e) {
            System.err.println("Error processing therapist message: " + e.getMessage());
            logger.log(Level.SEVERE, "Error processing therapist message: " + e.getMessage(), e);
            e.printStackTrace();
            throw e; // Re-throw to trigger message retry if needed
        }
    }
    
    /**
     * Parse JSON message into SimpleTherapistData object (direct format)
     */
    private SimpleTherapistData parseSimpleTherapistData(String jsonMessage) {
        try {
            System.out.println("Parsing simple therapist JSON message...");
            System.out.println("JSON content: " + jsonMessage);
            logger.info("Parsing simple therapist JSON message: " + jsonMessage);
            
            try (Jsonb jsonb = JsonbBuilder.create()) {
                SimpleTherapistData data = jsonb.fromJson(jsonMessage, SimpleTherapistData.class);
                System.out.println("Successfully parsed simple therapist data for: " + data.firstNames() + " " + data.paternalSurname());
                logger.info("Successfully parsed simple therapist data for: " + data.firstNames() + " " + data.paternalSurname());
                return data;
            }
        } catch (Exception e) {
            System.err.println("Error parsing simple therapist JSON message: " + e.getMessage());
            System.err.println("JSON that failed to parse: " + jsonMessage);
            logger.log(Level.SEVERE, "Error parsing simple therapist JSON message: " + e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Process simple therapist data using the service
     */
    private void processSimpleTherapistData(SimpleTherapistData therapistData) {
        try {
            System.out.println("Processing simple therapist data for: " + therapistData.firstNames() + " " + therapistData.paternalSurname());
            logger.info("Processing simple therapist data for: " + therapistData.firstNames() + " " + therapistData.paternalSurname());
            
            // Get the service using CDI programmatic lookup
            TherapistMessageProcessingService processingService = CDI.current().select(TherapistMessageProcessingService.class).get();
            
            // Process the simple therapist data directly
            processingService.processTherapistMessage(therapistData);
            
            System.out.println("Simple therapist profile created successfully in database");
            logger.info("Simple therapist profile created successfully in database");
            
        } catch (Exception e) {
            System.err.println("Error processing simple therapist data: " + e.getMessage());
            logger.log(Level.SEVERE, "Error processing simple therapist data: " + e.getMessage(), e);
            e.printStackTrace();
            throw e; // Re-throw to trigger message retry if needed
        }
    }
}