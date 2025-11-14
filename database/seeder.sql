-- Seeder para base de datos de Profiles
-- Datos mock coherentes con relaciones entre pacientes, responsables legales y terapeutas
-- Ejecutar después de la creación de tablas: mysql -u profiles_user -p profiles_db < seeder.sql

USE profiles_db;

-- Limpiar datos existentes (opcional - comentar si no quieres borrar datos)
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM patient_profiles;
DELETE FROM legal_responsible_profiles;
DELETE FROM therapist_profiles;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- TERAPEUTAS (deben crearse primero)
-- =============================================

INSERT INTO therapist_profiles (first_names, paternal_surname, maternal_surname, document_type, identity_document_number, email, phone, specialty_name, attention_place_address) VALUES 
('Ana Sofía', 'Rodríguez', 'Martínez', 'DNI', '25468731', 'ana.rodriguez@terapiaclinica.com', '+51987654321', 'Psicología Clínica', 'Centro de Salud Mental, Av. Arequipa 1245, Miraflores, Lima'),
('Carlos Eduardo', 'García', 'López', 'DNI', '26589473', 'carlos.garcia@infancia.com', '+51965432187', 'Psicología Infantil', 'Clínica Pediátrica San Juan, Av. Brasil 567, Magdalena, Lima'),
('María Elena', 'Vásquez', 'Torres', 'DNI', '24753698', 'maria.vasquez@familiavida.com', '+51978541236', 'Terapia Familiar', 'Instituto Familiar Integral, Calle Las Flores 890, San Isidro, Lima'),
('Luis Fernando', 'Mendoza', 'Ruiz', 'DNI', '27841596', 'luis.mendoza@neuropsico.com', '+51945782163', 'Neuropsicología', 'Centro de Neurociencias, Av. Javier Prado 1580, San Borja, Lima'),
('Patricia', 'Herrera', 'Silva', 'DNI', '25963741', 'patricia.herrera@educativa.com', '+51932658741', 'Psicología Educativa', 'Centro Educativo Terapéutico, Av. Universitaria 2456, Los Olivos, Lima');

-- =============================================
-- RESPONSABLES LEGALES
-- =============================================

INSERT INTO legal_responsible_profiles (first_names, paternal_surname, maternal_surname, document_type, identity_document_number, email, phone, relationship) VALUES
('Carmen Rosa', 'Pérez', 'González', 'DNI', '18456723', 'carmen.perez@gmail.com', '+51987123456', 'Madre'),
('Roberto Carlos', 'Jiménez', 'Morales', 'DNI', '19567834', 'roberto.jimenez@hotmail.com', '+51976234567', 'Padre'),
('Rosa María', 'Castro', 'Vargas', 'DNI', '15234567', 'rosa.castro@yahoo.com', '+51965345678', 'Abuela'),
('Ana Lucía', 'Fernández', 'Delgado', 'DNI', '20345678', 'ana.fernandez@gmail.com', '+51954456789', 'Madre'),
('Miguel Ángel', 'Sánchez', 'Reyes', 'DNI', '21456789', 'miguel.sanchez@outlook.com', '+51943567890', 'Tutor');

-- =============================================
-- PACIENTES
-- =============================================

INSERT INTO patient_profiles (first_names, paternal_surname, maternal_surname, document_type, identity_document_number, email, phone, birth_date, birth_place, current_age, first_appointment_age, gender, marital_status, current_address, district, province, region, country, religion, education_level, occupation, current_educational_institution, legal_responsible_id, therapist_id) VALUES
('Diego Alejandro', 'Pérez', 'Martín', 'DNI', '85647392', 'contacto.diego@familia-perez.com', '+51987123456', '2016-03-15', 'Lima, Perú', 8, 7, 'MASCULINO', 'SOLTERO', 'Jr. Los Cedros 456, Urb. Santa María', 'Miraflores', 'Lima', 'Lima', 'Perú', 'CATOLICO', 'Primaria', 'Estudiante', 'Colegio San Agustín', 1, 2),
('Valentina', 'Jiménez', 'Torres', 'DNI', '07829453', 'valentina.jimenez@estudiantil.com', '+51976234567', '2008-07-22', 'Lima, Perú', 16, 15, 'FEMENINO', 'SOLTERO', 'Av. La Marina 1234, Dpto 501', 'San Miguel', 'Lima', 'Lima', 'Perú', 'CATOLICO', 'Secundaria', 'Estudiante', 'IE Rosa de Santa María', 2, 1),
('Isabella Sofia', 'Castro', 'Mendoza', 'DNI', '18394756', 'contacto.isabella@familia-castro.com', '+51965345678', '2018-11-08', 'Lima, Perú', 6, 5, 'FEMENINO', 'SOLTERO', 'Calle Las Magnolias 789', 'Magdalena', 'Lima', 'Lima', 'Perú', 'EVANGELICO', 'Inicial', 'Estudiante', 'Jardín Pequeños Genios', 3, 2),
('Mateo Sebastián', 'Rivera', 'Campos', 'DNI', '74829456', 'mateo.rivera@universitario.edu.pe', '+51945678123', '2004-12-03', 'Lima, Perú', 20, 19, 'MASCULINO', 'SOLTERO', 'Av. Universitaria 2567, Casa 12', 'Los Olivos', 'Lima', 'Lima', 'Perú', 'CATOLICO', 'Universitario', 'Estudiante', 'Universidad Nacional Mayor de San Marcos', NULL, 3),
('Camila Alejandra', 'Fernández', 'Romero', 'DNI', '10583947', 'camila.fernandez@colegio.edu.pe', '+51954456789', '2010-05-18', 'Lima, Perú', 14, 13, 'FEMENINO', 'SOLTERO', 'Jr. Santa Rosa 345, Urb. El Bosque', 'San Isidro', 'Lima', 'Lima', 'Perú', 'CATOLICO', 'Secundaria', 'Estudiante', 'Colegio Villa María', 4, 3),
('Adrián Gabriel', 'Morales', 'Vega', 'DNI', '14728395', 'contacto.adrian@tutoria-sanchez.com', '+51943567890', '2014-09-25', 'Lima, Perú', 10, 9, 'MASCULINO', 'SOLTERO', 'Av. Los Álamos 1678, Dpto. 204', 'San Borja', 'Lima', 'Lima', 'Perú', 'CATOLICO', 'Primaria', 'Estudiante', 'Colegio Salesiano', 5, 4),
('Lucia Beatriz', 'Vargas', 'Herrera', 'DNI', '46829573', 'lucia.vargas@profesional.com', '+51923456789', '1996-08-14', 'Lima, Perú', 28, 27, 'FEMENINO', 'CASADO', 'Calle Los Rosales 567, Casa 8', 'Miraflores', 'Lima', 'Lima', 'Perú', 'CATOLICO', 'Universitario', 'Psicóloga', 'Universidad Cayetano Heredia', NULL, 1),
('Fernando José', 'Gutiérrez', 'Paredes', 'DNI', '35729481', 'fernando.gutierrez@empresa.com', '+51912345678', '1989-01-30', 'Lima, Perú', 35, 34, 'MASCULINO', 'CASADO', 'Av. El Sol 890, Torre B, Piso 15', 'San Isidro', 'Lima', 'Lima', 'Perú', 'CATOLICO', 'Universitario', 'Ingeniero', 'Universidad de Ingeniería', NULL, 5);

-- =============================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- =============================================

-- Mostrar resumen de datos insertados
SELECT 'TERAPEUTAS INSERTADOS' as tabla, COUNT(*) as cantidad FROM therapist_profiles
UNION ALL
SELECT 'RESPONSABLES LEGALES INSERTADOS', COUNT(*) FROM legal_responsible_profiles  
UNION ALL
SELECT 'PACIENTES INSERTADOS', COUNT(*) FROM patient_profiles;

-- Mostrar relaciones paciente-responsable-terapeuta
SELECT 
    CONCAT(p.first_names, ' ', p.paternal_surname) as paciente,
    p.current_age as edad,
    CONCAT(lr.first_names, ' ', lr.paternal_surname) as responsable_legal,
    lr.relationship,
    CONCAT(t.first_names, ' ', t.paternal_surname) as terapeuta,
    t.specialty_name
FROM patient_profiles p
LEFT JOIN legal_responsible_profiles lr ON p.legal_responsible_id = lr.id
LEFT JOIN therapist_profiles t ON p.therapist_id = t.id
ORDER BY p.current_age;

SELECT 'Datos de seeder insertados exitosamente' as status;