package com.soulware.therapysystem.profiles.profiles.interfaces.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;

/**
 * JAX-RS Application class for the Profiles bounded context.
 * This class configures the REST API endpoints for the therapy system profiles.
 */
@ApplicationPath("/profiles")
public class ProfilesApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
            PatientProfileController.class,
            LegalResponsibleProfileController.class,
            TherapistProfileController.class
        );
    }
}