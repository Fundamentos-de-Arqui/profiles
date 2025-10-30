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
import java.util.ArrayList;

import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetPatientProfileByIdQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetLegalResponsibleProfileByIdQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;

@WebListener
public class AppointmentDataActiveMQListener implements ServletContextListener, MessageListener {
    private static final Logger logger = Logger.getLogger(AppointmentDataActiveMQListener.class.getName());
    private static Connection connection;
    private static Session session;
    private static MessageConsumer consumer;
    private static MessageProducer producer;
    private static boolean isInitialized = false;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        synchronized (AppointmentDataActiveMQListener.class) {
            if (isInitialized) {
                logger.info("AppointmentDataActiveMQListener already initialized, skipping...");
                return;
            }
            try {
                closeExistingConnections();
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                connection = connectionFactory.createConnection();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue inputQueue = session.createQueue("profiles_getAppointmentData");
                Queue outputQueue = session.createQueue("apigateway_patientData");
                consumer = session.createConsumer(inputQueue);
                producer = session.createProducer(outputQueue);
                consumer.setMessageListener(this);
                connection.start();
                isInitialized = true;
                logger.info("SUCCESS: AppointmentDataActiveMQListener connected to profiles_getAppointmentData and ready to send to apigateway_patientData");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "FAILED to initialize AppointmentDataActiveMQListener: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== AppointmentDataActiveMQListener STOPPING ===");
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
            logger.info("=== MESSAGE RECEIVED BY APPOINTMENT DATA LISTENER ===");
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
            AppointmentRequest[] appointmentArray = jsonb.fromJson(messageText, AppointmentRequest[].class);
            List<AppointmentRequest> appointments = List.of(appointmentArray);
            
            if (appointments == null || appointments.isEmpty()) {
                logger.warning("Invalid or empty appointment list: " + messageText);
                return;
            }

            // Obtener servicios
            PatientProfileQueryService patientQueryService = CDI.current().select(PatientProfileQueryService.class).get();
            LegalResponsibleProfileQueryService legalQueryService = CDI.current().select(LegalResponsibleProfileQueryService.class).get();

            List<PatientAppointmentData> responseData = new ArrayList<>();

            // Procesar cada cita
            for (AppointmentRequest appointment : appointments) {
                if (appointment.patientId == null) {
                    logger.warning("Skipping appointment with null patientId: " + appointment.id);
                    continue;
                }

                // Buscar paciente
                GetPatientProfileByIdQuery patientQuery = new GetPatientProfileByIdQuery(appointment.patientId);
                PatientProfile patient = patientQueryService.handle(patientQuery).orElse(null);
                
                if (patient == null) {
                    logger.warning("Patient not found for ID: " + appointment.patientId);
                    continue;
                }

                // Obtener nombres del responsable legal y su teléfono
                String legalResponsibleName = null;
                String legalResponsiblePhone = null;

                if (patient.getLegalResponsibleId() != null) {
                    GetLegalResponsibleProfileByIdQuery legalQuery = new GetLegalResponsibleProfileByIdQuery(patient.getLegalResponsibleId());
                    LegalResponsibleProfile legalResponsible = legalQueryService.handle(legalQuery).orElse(null);
                    if (legalResponsible != null) {
                        legalResponsibleName = legalResponsible.getIdentity().firstNames().value() + " " + 
                                             legalResponsible.getIdentity().paternalSurname().value() + 
                                             (legalResponsible.getIdentity().maternalSurname() != null ? 
                                              " " + legalResponsible.getIdentity().maternalSurname().value() : "");
                        legalResponsiblePhone = legalResponsible.getIdentity().phone().value();
                    }
                }

                // Crear respuesta
                PatientAppointmentData patientData = new PatientAppointmentData(
                    patient.getIdentity().firstNames().value() + " " + 
                    patient.getIdentity().paternalSurname().value() + 
                    (patient.getIdentity().maternalSurname() != null ? 
                     " " + patient.getIdentity().maternalSurname().value() : ""),
                    patient.getId().value(),
                    patient.getIdentity().documentType().value(),
                    patient.getIdentity().identityDocumentNumber().value(),
                    legalResponsibleName,
                    legalResponsiblePhone,
                    appointment.scheduledAt
                );

                responseData.add(patientData);
            }

            // Enviar respuesta
            String responseJson = jsonb.toJson(responseData);
            TextMessage responseMessage = session.createTextMessage(responseJson);
            producer.send(responseMessage);
            logger.info("Sent patient appointment data to apigateway_patientData for " + responseData.size() + " patients");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing appointment data message: " + e.getMessage(), e);
        }
    }

    // DTO para deserializar las citas recibidas
    public static class AppointmentRequest {
        public Integer id;
        public Integer patientId;
        public String scheduledAt;
        public String status;
    }

    // DTO para la respuesta con datos del paciente
    public static class PatientAppointmentData {
        public String name;
        public Integer id;
        public String documentType;
        public String documentNumber;
        public String legalResponsible;
        public String legalResponsiblePhone;
        public String scheduledAt;

        public PatientAppointmentData(String name, Integer id, String documentType, String documentNumber, 
                                    String legalResponsible, String legalResponsiblePhone, String scheduledAt) {
            this.name = name;
            this.id = id;
            this.documentType = documentType;
            this.documentNumber = documentNumber;
            this.legalResponsible = legalResponsible;
            this.legalResponsiblePhone = legalResponsiblePhone;
            this.scheduledAt = scheduledAt;
        }
    }
}