Feature: Registro de pacientes en el sistema
  Como administrador del sistema
  Quiero registrar nuevos pacientes
  Para mantener actualizada la base de datos de usuarios y sus responsables legales

  Scenario: Registrar paciente en el sistema
    Given que el administrador ha ingresado al panel de administración
    And ha seleccionado la opción de registrar un nuevo paciente
    And ha completado correctamente los datos de identificación del paciente
    When confirma la adición del nuevo paciente
    Then el sistema debe registrar al nuevo paciente
    And el paciente debe quedar vinculado a un responsable legal
