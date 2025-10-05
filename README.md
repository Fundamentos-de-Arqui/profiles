# Profiles Bounded Context - Therapy System

## Descripción
Bounded Context para la gestión de perfiles de pacientes, responsables legales y terapeutas en el sistema de terapia. Implementa arquitectura con Domain-Driven Design (DDD) y CQRS pattern.

## Tecnologías
- **Java 17**
- **Jakarta EE 11** (CDI, JAX-RS, JPA, JSON-B, Servlet API)
- **WildFly 36** (Application Server)
- **MySQL 8.0+** (Base de datos relacional)
- **ActiveMQ 5.18.3** (Message Broker)
- **Maven** (Build tool)

## Arquitectura

### Capas implementadas:

1. **Domain Layer** - Lógica de negocio y modelo de dominio
   - 21 Value Objects (Identity, Address, BirthData, Age, etc.)
   - 3 Aggregates (PatientProfile, LegalResponsibleProfile, TherapistProfile)
   - Commands y Queries (CQRS pattern)
   - Domain Services interfaces
   - ProfileFactory para creación de objetos

2. **Infrastructure Layer** - Persistencia y comunicación
   - Repository implementations con JPA
   - ActiveMQ messaging infrastructure
   - ServletActiveMQListener para consumo de mensajes

3. **Application Layer** - Servicios de aplicación
   - Command Service implementations
   - Query Service implementations
   - Anti-Corruption Layer (ACL)
   - Message processing services

4. **Interfaces Layer** - API REST
   - JAX-RS Controllers
   - Resources (DTOs)
   - Transform Assemblers

## Estructura del Proyecto

```
src/main/java/com/soulware/therapysystem/profiles/profiles/
├── domain/
│   ├── model/
│   │   ├── aggregates/     # PatientProfile, LegalResponsibleProfile, TherapistProfile
│   │   ├── commands/       # Create/Delete commands
│   │   ├── queries/        # GetAll/GetById queries
│   │   ├── valueobjects/   # 21 value objects (Identity, Address, etc.)
│   │   ├── repositories/   # Repository interfaces
│   │   └── factories/      # ProfileFactory
│   └── services/           # Domain service interfaces
├── infrastructure/
│   ├── persistence/jpa/
│   │   └── repositories/   # JPA repository implementations
│   └── messaging/          # ActiveMQ messaging infrastructure
│       ├── listeners/      # ServletContextListener for ActiveMQ
│       ├── dto/           # Message DTOs
│       └── services/      # Message processing services
├── application/
│   ├── commandservices/   # Command service implementations
│   ├── queryservices/     # Query service implementations
│   └── acl/              # Anti-Corruption Layer implementation
└── interfaces/
    ├── rest/
    │   ├── resources/     # DTOs para request/response
    │   ├── transform/     # Assemblers para conversión
    │   └── [controllers]  # JAX-RS REST controllers
    └── acl/              # ACL interface definition
```

## API REST Endpoints

**Base URL**: `http://localhost:8080/Profiles-1.0-SNAPSHOT/profiles`

### Patient Profiles

#### Crear perfil de paciente
```http
POST /profiles/v1/patient-profiles
Content-Type: application/json

{
  "firstNames": "Juan Carlos",
  "paternalSurname": "Pérez",
  "maternalSurname": "García",
  "identityDocumentNumber": "12345678",
  "documentType": "DNI",
  "phone": "987654321",
  "email": "juan.perez@email.com",
  "birthPlace": "Lima",
  "birthDate": "1990-05-15",
  "firstAppointmentAge": 25,
  "currentAge": 33,
  "gender": "MALE",
  "maritalStatus": "SINGLE",
  "currentAddress": "Av. Principal 123",
  "district": "Miraflores",
  "province": "Lima",
  "region": "Lima",
  "country": "Perú",
  "religion": "CHRISTIANITY",
  "educationLevel": "Universitario",
  "occupation": "Ingeniero",
  "currentEducationalInstitution": "Universidad Nacional"
}
```

#### Obtener todos los perfiles de pacientes
```http
GET /profiles/v1/patient-profiles
```

#### Obtener perfil de paciente por ID
```http
GET /profiles/v1/patient-profiles/{id}
```

#### Eliminar perfil de paciente
```http
DELETE /profiles/v1/patient-profiles/{id}
```

### Legal Responsible Profiles

#### Crear perfil de responsable legal
```http
POST /profiles/v1/legal-responsible-profiles
Content-Type: application/json

{
  "firstNames": "María Elena",
  "paternalSurname": "González",
  "maternalSurname": "López",
  "identityDocumentNumber": "87654321",
  "documentType": "DNI",
  "phone": "912345678",
  "email": "maria.gonzalez@email.com",
  "relationship": "Madre"
}
```

#### Obtener todos los perfiles de responsables legales
```http
GET /profiles/v1/legal-responsible-profiles
```

#### Obtener perfil de responsable legal por ID
```http
GET /profiles/v1/legal-responsible-profiles/{id}
```

#### Eliminar perfil de responsable legal
```http
DELETE /profiles/v1/legal-responsible-profiles/{id}
```

### Therapist Profiles

#### Crear perfil de terapeuta
```http
POST /profiles/v1/therapist-profiles
Content-Type: application/json

{
  "firstNames": "Ana Sofía",
  "paternalSurname": "Rodríguez",
  "maternalSurname": "Martínez",
  "identityDocumentNumber": "11223344",
  "documentType": "DNI",
  "phone": "956789123",
  "email": "ana.rodriguez@email.com",
  "specialtyName": "Psicología Clínica",
  "attentionPlaceAddress": "Consultorio Médico, Av. Salud 456"
}
```

#### Obtener todos los perfiles de terapeutas
```http
GET /profiles/v1/therapist-profiles
```

#### Obtener perfil de terapeuta por ID
```http
GET /profiles/v1/therapist-profiles/{id}
```

#### Eliminar perfil de terapeuta
```http
DELETE /profiles/v1/therapist-profiles/{id}
```

## Enums Válidos

### Gender
- `MALE` - Masculino
- `FEMALE` - Femenino
- `OTHER` - Otro

### MaritalStatus
- `SINGLE` - Soltero/a
- `MARRIED` - Casado/a
- `DIVORCED` - Divorciado/a
- `WIDOWED` - Viudo/a
- `OTHER` - Otro

### Religion
- `JUDAISM` - Judaísmo
- `CHRISTIANITY` - Cristianismo
- `ISLAM` - Islam
- `BUDDHISM` - Budismo
- `OTHER` - Otra

## Anti-Corruption Layer (ACL)

El contexto expone una interfaz `ProfilesContextFacade` para que otros bounded contexts puedan acceder a información de perfiles sin acoplarse directamente al modelo interno.

### Interfaz disponible:

```java
public interface ProfilesContextFacade {
    
    /**
     * Buscar perfil de paciente por ID
     */
    Optional<PatientProfile> fetchPatientProfileById(Integer patientId);
    
    /**
     * Buscar perfil de paciente por tipo y número de documento
     */
    Optional<PatientProfile> fetchPatientProfileByDocument(String documentType, String documentNumber);
    
    /**
     * Verificar si existe un perfil de paciente
     */
    boolean patientProfileExists(Integer patientId);
}
```

### Uso desde otros contextos:

```java
@Inject
private ProfilesContextFacade profilesContext;

// Verificar si existe un paciente
boolean exists = profilesContext.patientProfileExists(123);

// Obtener información completa del paciente
Optional<PatientProfile> patient = profilesContext.fetchPatientProfileById(123);

// Buscar paciente por documento
Optional<PatientProfile> patient = profilesContext.fetchPatientProfileByDocument("DNI", "12345678");
```

## Mensajería JMS

El contexto consume mensajes del CustomerService a través de ActiveMQ para crear automáticamente perfiles de pacientes.

### Queue consumida:
- **Queue**: `patient.processing.queue`
- **Listener**: `ServletActiveMQListener`
- **Procesamiento**: Automático al recibir mensajes JSON

### Estructura del mensaje esperado:
```json
{
  "messageId": "uuid",
  "fileName": "archivo-excel",
  "uploadedAt": "2025-10-05T01:26:38.0847096",
  "patientData": {
    "firstNames": "Test Patient",
    "paternalSurname": "Apellido",
    "maternalSurname": "Materno",
    "documentType": "DNI",
    "documentNumber": "12345678",
    "phone": "987654321",
    "email": "test@email.com",
    "birthDate": "1990-01-15",
    "birthPlace": null,
    // ... otros campos opcionales
    "legalResponsibles": [],
    "therapists": null
  },
  "retryCount": 0,
  "status": "PENDING"
}
```

### Comportamiento del listener:
- ✅ Consume mensajes automáticamente
- ✅ Parsea JSON con Jakarta JSON-B
- ✅ Maneja valores null con valores por defecto
- ✅ Crea perfiles de paciente en la base de datos
- ✅ Registra logs detallados del procesamiento

## Instalación y Ejecución

### Prerrequisitos:
- Java 17+
- Maven 3.8+
- WildFly 36
- ActiveMQ 5.18.3

### Pasos:

1. **Compilar el proyecto:**
   ```bash
   ./mvnw clean package
   ```

2. **Desplegar en WildFly:**
   - Copiar `target/Profiles-1.0-SNAPSHOT.war` a `wildfly/standalone/deployments/`

3. **Configurar ActiveMQ:**
   - Iniciar ActiveMQ en puerto `61616`
   - Crear queue `patient.processing.queue`

4. **Verificar despliegue:**
   - API REST: `http://localhost:8080/Profiles-1.0-SNAPSHOT/profiles/v1/patient-profiles`
   - Logs: WildFly console para ver procesamiento de mensajes

## Base de Datos

- **Tipo**: H2 in-memory
- **Persistence Unit**: `profilesPU`
- **Configuración**: `META-INF/persistence.xml`
- **Inicialización**: Automática con JPA DDL

### Tablas principales:
- `patient_profiles`
- `legal_responsible_profiles`
- `therapist_profiles`

## Logging

El sistema genera logs detallados para:
- ✅ Procesamiento de mensajes JMS
- ✅ Creación/consulta de perfiles
- ✅ Errores de validación
- ✅ Operaciones de persistencia

Los logs aparecen en la consola de WildFly y ayudan a monitorear el funcionamiento del sistema.