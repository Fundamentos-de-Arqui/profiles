-- Script para crear la base de datos y usuario para Profiles microservice
-- Ejecutar como root o usuario con privilegios

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS profiles_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Crear el usuario para la aplicación
CREATE USER IF NOT EXISTS 'profiles_user'@'localhost' IDENTIFIED BY 'profiles_password';
CREATE USER IF NOT EXISTS 'profiles_user'@'%' IDENTIFIED BY 'profiles_password';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON profiles_db.* TO 'profiles_user'@'localhost';
GRANT ALL PRIVILEGES ON profiles_db.* TO 'profiles_user'@'%';

-- Aplicar los cambios
FLUSH PRIVILEGES;

-- Usar la base de datos
USE profiles_db;

-- Mostrar información
SELECT 'Base de datos profiles_db creada exitosamente' AS status;
SHOW GRANTS FOR 'profiles_user'@'localhost';