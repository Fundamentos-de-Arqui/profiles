package com.soulware.therapysystem.profiles.profiles.interfaces.rest;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateTherapistProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.DeleteTherapistProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetAllTherapistProfilesQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetTherapistProfileByIdQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetTherapistProfileByDocumentQuery;
import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.services.TherapistProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.CreateTherapistProfileResource;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.TherapistProfileResource;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform.CreateTherapistProfileCommandFromResourceAssembler;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform.TherapistProfileResourceFromEntityAssembler;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@Path("/v1/therapist-profiles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TherapistProfileController {

    @Inject
    private TherapistProfileCommandService commandService;
    
    @Inject
    private TherapistProfileQueryService queryService;

    // Default constructor for RESTEasy
    public TherapistProfileController() {
    }

    @POST
    public Response createTherapistProfile(CreateTherapistProfileResource resource) {
        try {
            System.out.println("[TherapistProfileController] Starting creation process for therapist profile");
            System.out.println("[TherapistProfileController] Resource received: " + resource);
            
            CreateTherapistProfileCommand command = 
                CreateTherapistProfileCommandFromResourceAssembler.toCommandFromResource(resource);
            
            System.out.println("[TherapistProfileController] Command created: " + command);
            
            Optional<TherapistProfile> optionalProfile = commandService.handle(command);
            
            System.out.println("[TherapistProfileController] Command service result: " + optionalProfile);
            
            if (optionalProfile.isEmpty()) {
                System.err.println("[TherapistProfileController] Command service returned empty result");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                              .entity("ERROR: Command service returned empty result - creation failed")
                              .build();
            }
            
            TherapistProfile createdProfile = optionalProfile.get();
            TherapistProfileResource responseResource = 
                TherapistProfileResourceFromEntityAssembler.toResourceFromEntity(createdProfile);
            
            System.out.println("[TherapistProfileController] Profile created successfully");
            return Response.status(Response.Status.CREATED)
                          .entity(responseResource)
                          .build();
        } catch (IllegalArgumentException e) {
            System.err.println("[TherapistProfileController] IllegalArgumentException: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity("ERROR: Invalid input data - " + e.getMessage())
                          .build();
        } catch (PersistenceException e) {
            // Check if it's a constraint violation by examining the message
            String message = e.getMessage();
            System.err.println("[TherapistProfileController] PersistenceException: " + message);
            e.printStackTrace();
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
                          .entity("ERROR: Unable to save therapist profile. Please try again or contact support.")
                          .build();
        } catch (Exception e) {
            System.err.println("[TherapistProfileController] Unexpected exception: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("ERROR: An unexpected error occurred while creating the therapist profile. Please try again.")
                          .build();
        }
    }

    @GET
    public Response getAllTherapistProfiles() {
        try {
            GetAllTherapistProfilesQuery query = new GetAllTherapistProfilesQuery();
            List<TherapistProfile> profiles = queryService.handle(query);
            
            List<TherapistProfileResource> resources = profiles.stream()
                .map(TherapistProfileResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
            
            return Response.ok(resources).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("Error retrieving therapist profiles")
                          .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getTherapistProfileById(@PathParam("id") Integer id) {
        try {
            GetTherapistProfileByIdQuery query = new GetTherapistProfileByIdQuery(id);
            Optional<TherapistProfile> optionalProfile = queryService.handle(query);
            
            if (optionalProfile.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                              .entity("Therapist profile not found")
                              .build();
            }
            
            TherapistProfile profile = optionalProfile.get();
            TherapistProfileResource resource = 
                TherapistProfileResourceFromEntityAssembler.toResourceFromEntity(profile);
            
            return Response.ok(resource).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity(e.getMessage())
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("Error retrieving therapist profile")
                          .build();
        }
    }

    @GET
    @Path("/document/{documentType}/{documentNumber}")
    public Response getTherapistProfileByDocument(@PathParam("documentType") String documentType, 
                                                   @PathParam("documentNumber") String documentNumber) {
        try {
            GetTherapistProfileByDocumentQuery query = new GetTherapistProfileByDocumentQuery(documentType, documentNumber);
            Optional<TherapistProfile> optionalProfile = queryService.handle(query);
            
            if (optionalProfile.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                              .entity("Therapist profile not found with document: " + documentType + " - " + documentNumber)
                              .build();
            }
            
            TherapistProfile profile = optionalProfile.get();
            TherapistProfileResource resource = 
                TherapistProfileResourceFromEntityAssembler.toResourceFromEntity(profile);
            
            return Response.ok(resource).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity("ERROR: Invalid input data - " + e.getMessage())
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("ERROR: Error retrieving therapist profile by document")
                          .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTherapistProfile(@PathParam("id") Integer id) {
        try {
            DeleteTherapistProfileCommand command = new DeleteTherapistProfileCommand(id);
            commandService.handle(command);
            
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity(e.getMessage())
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("Error deleting therapist profile")
                          .build();
        }
    }
}