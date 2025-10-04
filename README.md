# Profiles Bounded Context - Therapy System

Este es el bounded context de **Profiles** del sistema de terapia implementado siguiendo Domain-Driven Design (DDD) con CQRS pattern y Jakarta EE 11.

## Arquitectura

### Capas implementadas:

1. **Domain Layer** - Lógica de negocio y modelo de dominio
   - Value Objects (21 objetos de valor)
   - Aggregates (PatientProfile, LegalResponsibleProfile, TherapistProfile)
   - Commands y Queries (CQRS pattern)
   - Domain Services interfaces
   - Factory para creación de objetos

2. **Infrastructure Layer** - Persistencia y acceso a datos
   - Repository interfaces
   - Implementaciones JPA con EntityManager
   - Configuración de persistencia

3. **Application Layer** - Servicios de aplicación
   - Command Service implementations
   - Query Service implementations  
   - Dependency injection con CDI

4. **Interfaces Layer** - API REST y comunicación externa
   - JAX-RS Controllers
   - Resources (DTOs)
   - Transform Assemblers
   - Anti-Corruption Layer (ACL)

## Tecnologías

- **Jakarta EE 11** - Framework de aplicación empresarial
- **WildFly 36** - Servidor de aplicaciones
- **JDK 17** - Versión de Java
- **Maven** - Sistema de construcción
- **JPA/Hibernate** - Persistencia
- **H2 Database** - Base de datos en memoria (configurable)
- **CDI** - Dependency Injection
- **JAX-RS** - REST API

## Estructura del Proyecto

```
src/main/java/com/soulware/therapysystem/profiles/profiles/
├── domain/
│   ├── model/
│   │   ├── aggregates/     # PatientProfile, LegalResponsibleProfile, TherapistProfile
│   │   ├── commands/       # Create/Delete commands
│   │   ├── queries/        # GetAll/GetById queries
│   │   ├── valueobjects/   # 21 value objects (Identity, Address, etc.)
│   │   └── factories/      # ProfileFactory
│   └── services/           # Domain service interfaces
├── infrastructure/
│   └── persistence/jpa/
│       ├── repositories/   # JPA repository implementations
│       └── [interfaces]    # Repository interfaces
├── application/
│   ├── commandservices/   # Command service implementations
│   ├── queryservices/     # Query service implementations
│   └── acl/              # Anti-Corruption Layer implementation
└── interfaces/
    └── rest/
        ├── resources/     # DTOs para request/response
        ├── transform/     # Assemblers para conversión
        └── [controllers]  # JAX-RS REST controllers
```

## Endpoints REST

### Patient Profiles
- `POST /profiles/api/v1/patient-profiles` - Crear perfil de paciente
- `GET /profiles/api/v1/patient-profiles` - Obtener todos los perfiles
- `GET /profiles/api/v1/patient-profiles/{id}` - Obtener perfil por ID
- `DELETE /profiles/api/v1/patient-profiles/{id}` - Eliminar perfil

### Legal Responsible Profiles  
- `POST /profiles/api/v1/legal-responsible-profiles` - Crear perfil de responsable legal
- `GET /profiles/api/v1/legal-responsible-profiles` - Obtener todos los perfiles
- `GET /profiles/api/v1/legal-responsible-profiles/{id}` - Obtener perfil por ID
- `DELETE /profiles/api/v1/legal-responsible-profiles/{id}` - Eliminar perfil

### Therapist Profiles
- `POST /profiles/api/v1/therapist-profiles` - Crear perfil de terapeuta
- `GET /profiles/api/v1/therapist-profiles` - Obtener todos los perfiles
- `GET /profiles/api/v1/therapist-profiles/{id}` - Obtener perfil por ID
- `DELETE /profiles/api/v1/therapist-profiles/{id}` - Eliminar perfil

## Deployment en WildFly

### Prerrequisitos:
1. WildFly 36 instalado y corriendo
2. JDK 17 configurado
3. Maven instalado

### Pasos:

1. **Compilar el proyecto:**
   ```bash
   mvn clean package
   ```

2. **Configurar DataSource en WildFly:**
   ```bash
   # Conectar al CLI de WildFly
   ./jboss-cli.sh --connect
   
   # Agregar el DataSource H2
   /subsystem=datasources/data-source=ProfilesDS:add(jndi-name="java:jboss/datasources/ProfilesDS", connection-url="jdbc:h2:mem:profilesdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", driver-name=h2, user-name=sa, password=sa)
   /subsystem=datasources/data-source=ProfilesDS:enable
   ```

3. **Deploy la aplicación:**
   ```bash
   # Copiar el WAR al directorio de deployment
   cp target/Profiles-1.0-SNAPSHOT.war $WILDFLY_HOME/standalone/deployments/
   
   # O usar Maven plugin
   mvn wildfly:deploy
   ```

4. **Verificar deployment:**
   - Aplicación disponible en: `http://localhost:8080/Profiles-1.0-SNAPSHOT/`
   - API REST en: `http://localhost:8080/Profiles-1.0-SNAPSHOT/profiles/api/v1/`

## Ejemplos de uso

### Crear un Patient Profile:
```bash
curl -X POST http://localhost:8080/Profiles-1.0-SNAPSHOT/profiles/api/v1/patient-profiles \
  -H "Content-Type: application/json" \
  -d '{
    "firstNames": "Juan Carlos",
    "paternalSurname": "García",
    "maternalSurname": "López",
    "identityDocumentNumber": "12345678",
    "documentType": "DNI",
    "phone": "+51987654321",
    "email": "juan.garcia@email.com",
    "birthPlace": "Lima",
    "birthDate": "1990-05-15",
    "firstAppointmentAge": 25,
    "currentAge": 34,
    "gender": "MALE",
    "maritalStatus": "SINGLE",
    "currentAddress": "Av. Principal 123",
    "district": "Miraflores",
    "province": "Lima",
    "region": "Lima",
    "country": "Peru",
    "religion": "CATHOLIC",
    "educationLevel": "Universitario",
    "occupation": "Ingeniero",
    "currentEducationalInstitution": "Universidad Nacional"
  }'
```

### Obtener todos los Patient Profiles:
```bash
curl -X GET http://localhost:8080/Profiles-1.0-SNAPSHOT/profiles/api/v1/patient-profiles
```

## Configuración de Base de Datos

Por defecto usa H2 en memoria. Para usar PostgreSQL o MySQL:

1. **Agregar driver a WildFly**
2. **Modificar persistence.xml:**
   ```xml
   <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
   ```
3. **Actualizar DataSource en WildFly**

## Notas de Desarrollo

- **Value Objects inmutables** - Todos los value objects son inmutables usando records o clases finales
- **Aggregate boundaries** - Cada aggregate tiene su propio repositorio
- **CQRS separation** - Commands y Queries separados con servicios dedicados
- **CDI integration** - Dependency injection usando @ApplicationScoped y @Inject
- **JPA mapping** - Agregados mapeados como entidades JPA con campos primitivos para mejor rendimiento
- **Exception handling** - Manejo de errores en controllers con códigos HTTP apropiados

## Testing

Para ejecutar tests:
```bash
mvn test
```

## Arquitectura de la Solución

Esta implementación sigue los principios de DDD:
- **Bounded Context**: Profiles claramente delimitado
- **Ubiquitous Language**: Términos del dominio de terapia
- **Aggregates**: Consistencia transaccional
- **Value Objects**: Inmutabilidad y validación
- **Domain Services**: Lógica de dominio compleja
- **Anti-Corruption Layer**: Protección del dominio