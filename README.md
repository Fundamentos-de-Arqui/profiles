# Profiles Bounded Context - Therapy System

## Descripción
Bounded Context para la gestión de perfiles de pacientes, responsables legales y terapeutas en el sistema de terapia. Implementa arquitectura con Domain-Driven Design (DDD) y CQRS pattern.

## Tecnologías
- **Java 17**
- **Jakarta EE 11** (CDI, JAX-RS, JPA, JSON-B, Servlet API)
- **WildFly 36** (Application Server)
- **MySQL 8.0** (Base de datos)
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

**Base URL**: `http://localhost:8080/Profiles-1.0-SNAPSHOT/api/v1/`

### Patient Profiles

#### Crear perfil de paciente
```http
POST /v1/patient-profiles
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
GET /v1/patient-profiles
```

#### Obtener perfil de paciente por ID
```http
GET /v1/patient-profiles/{id}
```

#### Obtener perfil de paciente por documento
```http
GET /v1/patient-profiles/document/{documentType}/{documentNumber}
```

#### Eliminar perfil de paciente
```http
DELETE /v1/patient-profiles/{id}
```

### Legal Responsible Profiles

#### Crear perfil de responsable legal
```http
POST /v1/legal-responsible-profiles
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
GET /v1/legal-responsible-profiles
```

#### Obtener perfil de responsable legal por ID
```http
GET /v1/legal-responsible-profiles/{id}
```

#### Obtener perfil de responsable legal por documento
```http
GET /v1/legal-responsible-profiles/document/{documentType}/{documentNumber}
```

#### Eliminar perfil de responsable legal
```http
DELETE /v1/legal-responsible-profiles/{id}
```

### Therapist Profiles

#### Crear perfil de terapeuta
```http
POST /v1/therapist-profiles
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
GET /v1/therapist-profiles
```

#### Obtener perfil de terapeuta por ID
```http
GET /v1/therapist-profiles/{id}
```

#### Obtener perfil de terapeuta por documento
```http
GET /v1/therapist-profiles/document/{documentType}/{documentNumber}
```

#### Eliminar perfil de terapeuta
```http
DELETE /v1/therapist-profiles/{id}
```

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

## Mensajería JMS - Listeners ActiveMQ

El contexto implementa un sistema completo de mensajería JMS con **7 listeners especializados** que procesan diferentes tipos de solicitudes de otros microservicios.

### 🔧 Configuración ActiveMQ
- **Broker URL**: `tcp://localhost:61616`
- **Auto-acknowledge**: Habilitado
- **Formatos soportados**: `TextMessage` y `BytesMessage`
- **Serialización**: Jakarta JSON-B

---

### 🆕 1. ProfileRegisterActiveMQListener

**Propósito**: Procesa solicitudes de registro de nuevos usuarios y crea perfiles correspondientes.

**Queue de entrada**: `profiles_register`  
**Queue de salida**: `iam_register`

#### Lógica de validación:
- ✅ Verifica si ya existe un perfil con el mismo DNI y rol
- ✅ Solo permite registro si no hay duplicados por rol específico
- ✅ Un DNI puede existir en diferentes roles (ej: mismo DNI como paciente y como responsable legal)

#### Tipos de registro soportados:

**Paciente:**
```json
{
  "password": "string",
  "firstNames": "Juan Carlos",
  "paternalSurname": "Pérez",
  "maternalSurname": "García",
  "identityDocumentNumber": "12345678",
  "documentType": "DNI",
  "phone": "+51987654321",
  "email": "juan.perez@email.com",
  "birthPlace": "Lima, Perú",
  "birthDate": "1990-05-15",
  "firstAppointmentAge": 25,
  "currentAge": 33,
  "gender": "MASCULINO",
  "maritalStatus": "SOLTERO",
  "currentAddress": "Av. Principal 123",
  "district": "Miraflores",
  "province": "Lima",
  "region": "Lima",
  "country": "Perú",
  "religion": "CATOLICO",
  "educationLevel": "Universitario",
  "occupation": "Ingeniero",
  "currentEducationalInstitution": "Universidad Nacional",
  "referredTherapistName": "Dr. García",
  "legalResponsibleId": "1",
  "therapistId": "2"
}
```

**Terapeuta:**
```json
{
  "password": "string",
  "firstNames": "Ana",
  "paternalSurname": "Torres",
  "maternalSurname": "Lopez",
  "identityDocumentNumber": "11223344",
  "documentType": "DNI",
  "phone": "977665544",
  "email": "ana.torres@example.com",
  "specialtyName": "Psicología",
  "attentionPlaceAddress": "Av. Salud 456"
}
```

**Responsable Legal:**
```json
{
  "password": "string",
  "documentType": "DNI",
  "email": "carmen.perez@gmail.com",
  "firstNames": "Carmen Rosa",
  "identityDocumentNumber": "18456723",
  "maternalSurname": "González",
  "paternalSurname": "Pérez",
  "phone": "+51987123456",
  "relationship": "Madre"
}
```

#### Formato de salida (hacia IAM):
```json
{
  "accountType": "PATIENT" | "LEGAL_RESPONSIBLE" | "THERAPIST",
  "password": "string",
  "documentType": "DNI",
  "identityDocumentNumber": "12345678"
}
```

---

### 📋 2. ExcelDataActiveMQListener

**Propósito**: Procesa solicitudes de datos de pacientes para generación de formularios Excel.

**Queue de entrada**: `profiles_getExcelData`  
**Queue de salida**: `excelParser_patientForm`

#### Formato de entrada:
```json
{
    "type": "DNI",
    "documentNumber": "12345678",
    "timestamp": "2025-11-02T10:15:30Z"
}
```

#### Formato de salida:
```json
{
    "id": 1,
    "firstNames": "Juan Carlos",
    "paternalSurname": "Pérez",
    "maternalSurname": "García",
    "documentType": "DNI",
    "identityDocumentNumber": "12345678",
    "email": "juan.perez@email.com",
    "phone": "987654321",
    "birthDate": "1990-05-15",
    "birthPlace": "Lima, Perú",
    "currentAge": 33,
    "firstAppointmentAge": 25,
    "gender": "MASCULINO",
    "maritalStatus": "SOLTERO",
    "currentAddress": "Av. Principal 123",
    "district": "Miraflores",
    "province": "Lima",
    "region": "Lima",
    "country": "Perú",
    "religion": "CATOLICO",
    "educationLevel": "Universitario",
    "occupation": "Ingeniero",
    "currentEducationalInstitution": "Universidad Nacional",
    "legalResponsible": {
        "id": 1,
        "firstNames": "María Elena",
        "paternalSurname": "González",
        "maternalSurname": "López",
        "documentType": "DNI",
        "identityDocumentNumber": "87654321",
        "email": "maria.gonzalez@email.com",
        "phone": "912345678",
        "relationship": "Madre"
    },
    "therapist": {
        "id": 1,
        "firstNames": "Ana Sofía",
        "paternalSurname": "Rodríguez",
        "maternalSurname": "Martínez",
        "documentType": "DNI",
        "identityDocumentNumber": "11223344",
        "email": "ana.rodriguez@email.com",
        "phone": "956789123",
        "specialtyName": "Psicología Clínica",
        "attentionPlaceAddress": "Consultorio Médico, Av. Salud 456"
    }
}
```

---

### 📅 3. AppointmentDataActiveMQListener

**Propósito**: Procesa listas de citas médicas y retorna datos resumidos de pacientes con información de paginación.

**Queue de entrada**: `profiles_getAppointmentData`  
**Queue de salida**: `apigateway_patientData`

#### Formato de entrada:
```json
{
    "folders": [
        {
            "id": 1,
            "status": "ACTIVE",
            "patientId": 1,
            "scheduledAt": "2007-12-03T10:15:30"
        },
        {
            "id": 2,
            "status": "ACTIVE",
            "patientId": 200,
            "scheduledAt": null
        }
    ],
    "totalPages": 1,
    "totalElements": 2,
    "currentPage": 0,
    "pageSize": 2
}
```

#### Formato de salida:
```json
{
    "totalResults": 2,
    "currentPage": 1,
    "maxPage": 1,
    "patients": [
        {
            "id": 1,
            "status": "ACTIVE",
            "name": "Juan Carlos Pérez García",
            "documentType": "DNI",
            "documentNumber": "12345678",
            "legalResponsible": "María Elena González López",
            "legalResponsiblePhone": "912345678",
            "scheduledAt": "2007-12-03T10:15:30"
        },
        {
            "id": 200,
            "status": "ACTIVE",
            "name": "Ana María López Martínez",
            "documentType": "DNI",
            "documentNumber": "87654321",
            "legalResponsible": null,
            "legalResponsiblePhone": null,
            "scheduledAt": null
        }
    ]
}
```

---

### 🏥 4. MedicalRecordActiveMQListener

**Propósito**: Procesa solicitudes de expedientes médicos y retorna datos completos del paciente y terapeuta asociados.

**Queue de entrada**: `profiles_getMedicalRecord`  
**Queue de salida**: `apigateway_filiationFiles`

#### Formato de entrada:
```json
{
    "id": 1,
    "versionNumber": 1,
    "diagnostic": "Ansiedad generalizada",
    "treatment": "Terapia cognitivo-conductual",
    "description": "Sesión inicial de evaluación",
    "patientId": 1,
    "therapistId": 1,
    "assessmentType": "ASSESSMENT",
    "scheduledAt": "2007-12-03T10:15:30",
    "createdAt": "2025-10-26T08:02:57Z"
}
```

#### Formato de salida:
```json
{
    "id": 1,
    "scheduledAt": "2007-12-03T10:15:30",
    "createdAt": "2025-10-26T08:02:57Z",
    "assessmentType": "ASSESSMENT",
    "description": "Sesión inicial de evaluación",
    "diagnostic": "Ansiedad generalizada",
    "treatment": "Terapia cognitivo-conductual",
    "versionNumber": 1,
    "patient": {
        "birthDate": "1990-05-15",
        "birthPlace": "Lima, Perú",
        "country": "Perú",
        "currentAddress": "Av. Principal 123",
        "currentAge": 33,
        "currentEducationalInstitution": "Universidad Nacional",
        "district": "Miraflores",
        "documentType": "DNI",
        "educationLevel": "Universitario",
        "email": "juan.perez@email.com",
        "firstAppointmentAge": 25,
        "firstNames": "Juan Carlos",
        "gender": "MASCULINO",
        "identityDocumentNumber": "12345678",
        "maritalStatus": "SOLTERO",
        "maternalSurname": "García",
        "occupation": "Ingeniero",
        "paternalSurname": "Pérez",
        "phone": "987654321",
        "province": "Lima",
        "region": "Lima",
        "religion": "CATOLICO"
    },
    "therapist": {
        "attentionPlaceAddress": "Consultorio Médico, Av. Salud 456",
        "documentType": "DNI",
        "email": "ana.rodriguez@email.com",
        "firstNames": "Ana Sofía",
        "identityDocumentNumber": "11223344",
        "maternalSurname": "Martínez",
        "paternalSurname": "Rodríguez",
        "phone": "956789123",
        "specialtyName": "Psicología Clínica"
    }
}
```

---

### 📋 5. TherapistProfilesActiveMQListener

**Propósito**: Retorna la lista completa de perfiles de terapeutas registrados en el sistema.

**Queue de entrada**: `profiles_therapistProfiles`  
**Queue de salida**: `apigateway_therapistProfiles`

#### Formato de entrada:
```json
{
    "request": "getAllTherapists"
}
```

#### Formato de salida:
```json
[
    {
        "id": 1,
        "firstNames": "Ana Sofía",
        "paternalSurname": "Rodríguez",
        "maternalSurname": "Martínez",
        "identityDocumentNumber": "11223344",
        "documentType": "DNI",
        "phone": "956789123",
        "email": "ana.rodriguez@email.com",
        "specialtyName": "Psicología Clínica",
        "attentionPlaceAddress": "Consultorio Médico, Av. Salud 456"
    },
    {
        "id": 2,
        "firstNames": "Carlos Eduardo",
        "paternalSurname": "López",
        "maternalSurname": "García",
        "identityDocumentNumber": "55667788",
        "documentType": "DNI",
        "phone": "923456789",
        "email": "carlos.lopez@email.com",
        "specialtyName": "Terapia Familiar",
        "attentionPlaceAddress": "Centro de Salud Mental, Calle Bienestar 789"
    }
]
```

---

### 👨‍👩‍👧‍👦 6. LegalResponsibleProfilesActiveMQListener

**Propósito**: Retorna la lista completa de perfiles de responsables legales registrados en el sistema.

**Queue de entrada**: `profiles_legal-responsibleProfiles`  
**Queue de salida**: `apigateway_legal-responsibleProfiles`

#### Formato de entrada:
```json
{
    "request": "getAllLegalResponsibles"
}
```

#### Formato de salida:
```json
[
    {
        "id": 1,
        "firstNames": "María Elena",
        "paternalSurname": "González",
        "maternalSurname": "López",
        "identityDocumentNumber": "87654321",
        "documentType": "DNI",
        "phone": "912345678",
        "email": "maria.gonzalez@email.com",
        "relationship": "Madre"
    },
    {
        "id": 2,
        "firstNames": "Roberto Miguel",
        "paternalSurname": "Torres",
        "maternalSurname": "Ramírez",
        "identityDocumentNumber": "11445566",
        "documentType": "DNI",
        "phone": "987123456",
        "email": "roberto.torres@email.com",
        "relationship": "Padre"
    }
]
```

---

### 👤 7. ServletActiveMQListener (Legacy)

**Propósito**: Procesa mensajes del sistema de clientes para crear automáticamente perfiles de pacientes.

**Queue de entrada**: `patient.processing.queue`

#### Formato de entrada:
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
        "legalResponsibles": [],
        "therapists": null
    },
    "retryCount": 0,
    "status": "PENDING"
}
```

---

### 🔄 Flujo de Procesamiento

Todos los listeners siguen este patrón:

1. **Recepción**: Escuchan en sus respectivas colas de entrada
2. **Validación**: Verifican formato JSON y campos requeridos
3. **Conversión**: Soportan `TextMessage` y `BytesMessage`
4. **Procesamiento**: Consultan base de datos usando servicios de dominio
5. **Respuesta**: Envían JSON estructurado a colas de salida
6. **Logging**: Registran operaciones y errores detalladamente

### 🚨 Manejo de Errores

- **Datos no encontrados**: Se logea como WARNING y se omite el procesamiento
- **JSON inválido**: Se logea como WARNING y se rechaza el mensaje
- **Errores de conexión**: Se logea como SEVERE con stack trace
- **Tipos de mensaje no soportados**: Se logea como WARNING

### 📊 Monitoreo

Los listeners generan logs detallados para:
- ✅ Conexión exitosa a colas
- ✅ Mensajes recibidos y procesados
- ✅ Datos enviados a colas de salida
- ⚠️ Errores de validación
- ❌ Errores de procesamiento

Los logs aparecen en la consola de WildFly con formato:
```
[LOGGER_NAME] (Thread) MESSAGE_CONTENT
```

## Instalación y Ejecución

### Prerrequisitos:
- Java 17+
- Maven 3.8+
- WildFly 36
- MySQL 8.0+
- ActiveMQ 5.18.3

### Pasos:

1. **Configurar MySQL:**
   ```bash
   # Crear base de datos y usuario
   mysql -u root -p < database/setup-mysql.sql
   ```

2. **Configurar WildFly para MySQL:**
   ```powershell
   # En PowerShell (Windows)
   .\setup-mysql.ps1 -WildFlyHome "C:\path\to\wildfly"
   
   # O manualmente:
   # 1. Copiar MySQL module: wildfly-config/mysql-module.xml
   # 2. Descargar mysql-connector-j-8.0.33.jar
   # 3. Configurar en $WILDFLY_HOME/modules/system/layers/base/com/mysql/main/
   ```

3. **Iniciar WildFly y configurar DataSource:**
   ```bash
   # Iniciar WildFly
   $WILDFLY_HOME/bin/standalone.bat
   
   # En otra terminal, configurar DataSource
   $WILDFLY_HOME/bin/jboss-cli.bat --connect --file=mysql-datasource-setup.cli
   ```

4. **Compilar el proyecto:**
   ```bash
   ./mvnw clean package
   ```

5. **Desplegar en WildFly:**
   - Copiar `target/Profiles-1.0-SNAPSHOT.war` a `wildfly/standalone/deployments/`

3. **Configurar ActiveMQ:**
   - Iniciar ActiveMQ en puerto `61616`
   - Crear las siguientes colas:
     - `profiles_register` (entrada)
     - `iam_register` (salida) 
     - `profiles_getExcelData` (entrada)
     - `excelParser_patientForm` (salida)
     - `profiles_getAppointmentData` (entrada)
     - `apigateway_patientData` (salida)
     - `profiles_getMedicalRecord` (entrada)
     - `apigateway_filiationFiles` (salida)
     - `profiles_getSessions` (entrada)
     - `apigateway_getSessions` (salida)
     - `profiles_therapistProfiles` (entrada)
     - `apigateway_therapistProfiles` (salida)
     - `profiles_legal-responsibleProfiles` (entrada)
     - `apigateway_legal-responsibleProfiles` (salida)
     - `patient.processing.queue` (entrada - legacy)

4. **Verificar despliegue:**
   - API REST: `http://localhost:8080/Profiles-1.0-SNAPSHOT/api/v1/patient-profiles`
   - Logs: WildFly console para ver conexión de listeners JMS
   - ActiveMQ Web Console: `http://localhost:8161/admin` (admin/admin)
   - MySQL: Verificar conexión del DataSource en WildFly Admin Console: `http://localhost:9990`
   - Verificar que los 7 listeners se conecten exitosamente:
     ```
     SUCCESS: ProfileRegisterActiveMQListener connected to profiles_register
     SUCCESS: ExcelDataActiveMQListener connected to profiles_getExcelData
     SUCCESS: AppointmentDataActiveMQListener connected to profiles_getAppointmentData  
     SUCCESS: MedicalRecordActiveMQListener connected to profiles_getMedicalRecord
     SUCCESS: SessionProfileActiveMQListener connected to profile_getSessions
     SUCCESS: TherapistProfilesActiveMQListener connected to profiles_therapistProfiles
     SUCCESS: LegalResponsibleProfilesActiveMQListener connected to profiles_legal-responsibleProfiles
     SUCCESS: ServletActiveMQListener connected to patient.processing.queue
     ```

## Base de Datos

- **Tipo**: MySQL 8.0
- **Base de datos**: `profiles_db`
- **Usuario**: `profiles_user`
- **Contraseña**: `profiles_password`
- **Persistence Unit**: `profilesPU`
- **DataSource**: `java:jboss/datasources/ProfilesDS`
- **Configuración**: `META-INF/persistence.xml`
- **Inicialización**: Automática con JPA DDL (hibernate.hbm2ddl.auto=update)

### Tablas principales:
- `patient_profiles` - Perfiles de pacientes
- `legal_responsible_profiles` - Perfiles de responsables legales  
- `therapist_profiles` - Perfiles de terapeutas

### Configuración de conexión MySQL:
```
URL: jdbc:mysql://localhost:3306/profiles_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
Usuario: profiles_user
Contraseña: profiles_password
```

### Scripts incluidos:
- `database/setup-mysql.sql` - Inicialización de base de datos
- `mysql-datasource-setup.cli` - Configuración WildFly DataSource
- `setup-mysql.ps1` - Script automatizado de configuración

## Logging

El sistema genera logs detallados para:
- ✅ Inicialización y conexión de listeners JMS
- ✅ Procesamiento de mensajes en 5 colas diferentes  
- ✅ Validación de registros por DNI y rol
- ✅ Creación/consulta de perfiles (pacientes, terapeutas, responsables legales)
- ✅ Errores de validación y datos no encontrados
- ✅ Operaciones de persistencia JPA
- ✅ Conversión de mensajes (TextMessage ↔ BytesMessage)
- ✅ Envío de respuestas a colas de salida

### Ejemplos de logs importantes:
```
SUCCESS: ProfileRegisterActiveMQListener connected to profiles_register and ready to send to iam_register
Processing patient registration for DNI: 12345678
Patient with DNI 12345678 already exists. Registration aborted.
Patient registration completed for DNI: 87654321
Sent registration message to IAM: {"accountType":"PATIENT",...}
SUCCESS: ExcelDataActiveMQListener connected to profiles_getExcelData and ready to send to excelParser_patientForm
=== MESSAGE RECEIVED BY APPOINTMENT DATA LISTENER ===
Sent patient appointment data to apigateway_patientData for 2 patients
Patient not found for ID: 1
Sent medical record data to apigateway_filiationFiles for patient ID: 1 and therapist ID: 1
```

Los logs aparecen en la consola de WildFly y ayudan a monitorear el funcionamiento completo del sistema de mensajería.