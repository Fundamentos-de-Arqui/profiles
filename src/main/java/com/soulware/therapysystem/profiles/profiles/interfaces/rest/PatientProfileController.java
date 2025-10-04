package com.soulware.therapysystem.profiles.profiles.interfaces.rest;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreatePatientProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.DeletePatientProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetAllPatientProfilesQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetPatientProfileByIdQuery;
import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.services.PatientProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.CreatePatientProfileResource;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.PatientProfileResource;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform.CreatePatientProfileCommandFromResourceAssembler;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform.PatientProfileResourceFromEntityAssembler;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@Path("/v1/patient-profiles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientProfileController {

    @Inject
    private PatientProfileCommandService commandService;
    
    @Inject
    private PatientProfileQueryService queryService;

    // Default constructor for RESTEasy
    public PatientProfileController() {
    }

    @POST
    public Response createPatientProfile(CreatePatientProfileResource resource) {
        try {
            CreatePatientProfileCommand command = 
                CreatePatientProfileCommandFromResourceAssembler.toCommandFromResource(resource);
            
            Optional<PatientProfile> optionalProfile = commandService.handle(command);
            
            if (optionalProfile.isEmpty()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                              .entity("ERROR: Command service returned empty result - patient creation failed")
                              .build();
            }
            
            PatientProfile createdProfile = optionalProfile.get();
            PatientProfileResource responseResource = 
                PatientProfileResourceFromEntityAssembler.toResourceFromEntity(createdProfile);
            
            return Response.status(Response.Status.CREATED)
                          .entity(responseResource)
                          .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity("ERROR: Invalid input data - " + e.getMessage())
                          .build();
        } catch (PersistenceException e) {
            // Check if it's a constraint violation by examining the message
            String message = e.getMessage();
            if (message != null) {
                if (message.toLowerCase().contains("identity_document_number")) {
                    return Response.status(Response.Status.CONFLICT)
                                  .entity("ERROR: Document number already exists. Please use a different document number.")
                                  .build();
                } else if (message.toLowerCase().contains("email")) {
                    return Response.status(Response.Status.CONFLICT)
                                  .entity("ERROR: Email already exists. Please use a different email address.")
                                  .build();
                } else if (message.toLowerCase().contains("phone")) {
                    return Response.status(Response.Status.CONFLICT)
                                  .entity("ERROR: Phone number already exists. Please use a different phone number.")
                                  .build();
                } else if (message.toLowerCase().contains("constraint") || message.toLowerCase().contains("unique")) {
                    return Response.status(Response.Status.CONFLICT)
                                  .entity("ERROR: Data already exists. Please check your input data for duplicates.")
                                  .build();
                }
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("ERROR: Unable to save patient profile. Please try again or contact support.")
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("ERROR: An unexpected error occurred while creating the patient profile. Please try again.")
                          .build();
        }
    }

    @GET
    public Response getAllPatientProfiles() {
        try {
            GetAllPatientProfilesQuery query = new GetAllPatientProfilesQuery();
            List<PatientProfile> profiles = queryService.handle(query);
            
            List<PatientProfileResource> resources = profiles.stream()
                .map(PatientProfileResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
            
            return Response.ok(resources).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("Error retrieving patient profiles")
                          .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getPatientProfileById(@PathParam("id") Integer id) {
        try {
            GetPatientProfileByIdQuery query = new GetPatientProfileByIdQuery(id);
            Optional<PatientProfile> optionalProfile = queryService.handle(query);
            
            if (optionalProfile.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                              .entity("Patient profile not found")
                              .build();
            }
            
            PatientProfile profile = optionalProfile.get();
            PatientProfileResource resource = 
                PatientProfileResourceFromEntityAssembler.toResourceFromEntity(profile);
            
            return Response.ok(resource).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity(e.getMessage())
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("Error retrieving patient profile")
                          .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletePatientProfile(@PathParam("id") Integer id) {
        try {
            DeletePatientProfileCommand command = new DeletePatientProfileCommand(id);
            commandService.handle(command);
            
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity(e.getMessage())
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("Error deleting patient profile")
                          .build();
        }
    }
}