package com.soulware.therapysystem.profiles.profiles.domain.model.queries;

public record GetLegalResponsibleProfileByIdQuery(Integer legalResponsibleProfileId) {
    
    public GetLegalResponsibleProfileByIdQuery {
        if (legalResponsibleProfileId == null || legalResponsibleProfileId <= 0)
            throw new IllegalArgumentException("Legal responsible profile ID must be positive");
    }
}