package com.soulware.therapysystem.profiles.profiles.interfaces.rest;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.CreateLegalResponsibleProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.commands.DeleteLegalResponsibleProfileCommand;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetAllLegalResponsibleProfilesQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetLegalResponsibleProfileByIdQuery;
import com.soulware.therapysystem.profiles.profiles.domain.model.queries.GetLegalResponsibleProfileByDocumentQuery;
import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileCommandService;
import com.soulware.therapysystem.profiles.profiles.domain.services.LegalResponsibleProfileQueryService;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.CreateLegalResponsibleProfileResource;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.resources.LegalResponsibleProfileResource;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform.CreateLegalResponsibleProfileCommandFromResourceAssembler;
import com.soulware.therapysystem.profiles.profiles.interfaces.rest.transform.LegalResponsibleProfileResourceFromEntityAssembler;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@Path("/v1/legal-responsible-profiles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LegalResponsibleProfileController {

    @Inject
    private LegalResponsibleProfileCommandService commandService;
    
    @Inject
    private LegalResponsibleProfileQueryService queryService;

    // Default constructor for RESTEasy
    public LegalResponsibleProfileController() {
    }

    @POST
    public Response createLegalResponsibleProfile(CreateLegalResponsibleProfileResource resource) {
        try {
            CreateLegalResponsibleProfileCommand command = 
                CreateLegalResponsibleProfileCommandFromResourceAssembler.toCommandFromResource(resource);
            
            Optional<LegalResponsibleProfile> optionalProfile = commandService.handle(command);
            
            if (optionalProfile.isEmpty()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                              .entity("ERROR: Command service returned empty result - legal responsible creation failed")
                              .build();
            }
            
            LegalResponsibleProfile createdProfile = optionalProfile.get();
            LegalResponsibleProfileResource responseResource = 
                LegalResponsibleProfileResourceFromEntityAssembler.toResourceFromEntity(createdProfile);
            
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
                          .entity("ERROR: Unable to save legal responsible profile. Please try again or contact support.")
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("ERROR: An unexpected error occurred while creating the legal responsible profile. Please try again.")
                          .build();
        }
    }

    @GET
    public Response getAllLegalResponsibleProfiles() {
        try {
            GetAllLegalResponsibleProfilesQuery query = new GetAllLegalResponsibleProfilesQuery();
            List<LegalResponsibleProfile> profiles = queryService.handle(query);
            
            List<LegalResponsibleProfileResource> resources = profiles.stream()
                .map(LegalResponsibleProfileResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
            
            return Response.ok(resources).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("Error retrieving legal responsible profiles")
                          .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getLegalResponsibleProfileById(@PathParam("id") Integer id) {
        try {
            GetLegalResponsibleProfileByIdQuery query = new GetLegalResponsibleProfileByIdQuery(id);
            Optional<LegalResponsibleProfile> optionalProfile = queryService.handle(query);
            
            if (optionalProfile.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                              .entity("Legal responsible profile not found")
                              .build();
            }
            
            LegalResponsibleProfile profile = optionalProfile.get();
            LegalResponsibleProfileResource resource = 
                LegalResponsibleProfileResourceFromEntityAssembler.toResourceFromEntity(profile);
            
            return Response.ok(resource).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity(e.getMessage())
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("Error retrieving legal responsible profile")
                          .build();
        }
    }

    @GET
    @Path("/document/{documentType}/{documentNumber}")
    public Response getLegalResponsibleProfileByDocument(@PathParam("documentType") String documentType, 
                                                         @PathParam("documentNumber") String documentNumber) {
        try {
            GetLegalResponsibleProfileByDocumentQuery query = new GetLegalResponsibleProfileByDocumentQuery(documentType, documentNumber);
            Optional<LegalResponsibleProfile> optionalProfile = queryService.handle(query);
            
            if (optionalProfile.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                              .entity("Legal responsible profile not found with document: " + documentType + " - " + documentNumber)
                              .build();
            }
            
            LegalResponsibleProfile profile = optionalProfile.get();
            LegalResponsibleProfileResource resource = 
                LegalResponsibleProfileResourceFromEntityAssembler.toResourceFromEntity(profile);
            
            return Response.ok(resource).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity("ERROR: Invalid input data - " + e.getMessage())
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("ERROR: Error retrieving legal responsible profile by document")
                          .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteLegalResponsibleProfile(@PathParam("id") Integer id) {
        try {
            DeleteLegalResponsibleProfileCommand command = new DeleteLegalResponsibleProfileCommand(id);
            commandService.handle(command);
            
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity(e.getMessage())
                          .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("Error deleting legal responsible profile")
                          .build();
        }
    }
}