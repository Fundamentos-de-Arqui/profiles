package com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.LegalResponsibleProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.LegalResponsibleProfileId;
import com.soulware.therapysystem.profiles.profiles.domain.model.repositories.LegalResponsibleProfileRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LegalResponsibleProfileRepositoryImpl implements LegalResponsibleProfileRepository {

    @PersistenceContext(unitName = "profilesPU")
    private EntityManager entityManager;

    public LegalResponsibleProfileRepositoryImpl(){}

    public LegalResponsibleProfileRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public LegalResponsibleProfile save(LegalResponsibleProfile legalResponsibleProfile) {
        LegalResponsibleProfileId profileId = legalResponsibleProfile.getId();
        if (profileId == null) {
            // Nueva entidad - usar persist
            entityManager.persist(legalResponsibleProfile);
            return legalResponsibleProfile;
        } else {
            // Entidad existente - usar merge
            return entityManager.merge(legalResponsibleProfile);
        }
    }

    @Override
    public Optional<LegalResponsibleProfile> findById(LegalResponsibleProfileId id) {
        LegalResponsibleProfile profile = entityManager.find(LegalResponsibleProfile.class, id.value());
        return Optional.ofNullable(profile);
    }

    @Override
    public List<LegalResponsibleProfile> findAll() {
        TypedQuery<LegalResponsibleProfile> query = entityManager.createQuery(
            "SELECT l FROM LegalResponsibleProfile l", LegalResponsibleProfile.class);
        return query.getResultList();
    }

    @Override
    public void delete(LegalResponsibleProfile legalResponsibleProfile) {
        if (entityManager.contains(legalResponsibleProfile)) {
            entityManager.remove(legalResponsibleProfile);
        } else {
            LegalResponsibleProfile managed = entityManager.merge(legalResponsibleProfile);
            entityManager.remove(managed);
        }
    }

    @Override
    public void deleteById(LegalResponsibleProfileId id) {
        Optional<LegalResponsibleProfile> profile = findById(id);
        profile.ifPresent(this::delete);
    }

    @Override
    public Optional<LegalResponsibleProfile> findByDocumentTypeAndNumber(String documentType, String documentNumber) {
        TypedQuery<LegalResponsibleProfile> query = entityManager.createQuery(
            "SELECT l FROM LegalResponsibleProfile l WHERE l.documentType = :documentType AND l.identityDocumentNumber = :documentNumber", 
            LegalResponsibleProfile.class);
        query.setParameter("documentType", documentType);
        query.setParameter("documentNumber", documentNumber);
        List<LegalResponsibleProfile> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}