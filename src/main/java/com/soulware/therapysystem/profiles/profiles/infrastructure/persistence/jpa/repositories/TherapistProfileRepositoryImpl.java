package com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.TherapistProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.TherapistProfileId;
import com.soulware.therapysystem.profiles.profiles.domain.model.repositories.TherapistProfileRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TherapistProfileRepositoryImpl implements TherapistProfileRepository {

    @PersistenceContext(unitName = "profilesPU")
    private EntityManager entityManager;

    @Override
    public TherapistProfile save(TherapistProfile therapistProfile) {
        TherapistProfileId profileId = therapistProfile.getId();
        if (profileId == null) {
            // Nueva entidad - usar persist
            entityManager.persist(therapistProfile);
            return therapistProfile;
        } else {
            // Entidad existente - usar merge
            return entityManager.merge(therapistProfile);
        }
    }

    @Override
    public Optional<TherapistProfile> findById(TherapistProfileId id) {
        TherapistProfile profile = entityManager.find(TherapistProfile.class, id.value());
        return Optional.ofNullable(profile);
    }

    @Override
    public List<TherapistProfile> findAll() {
        TypedQuery<TherapistProfile> query = entityManager.createQuery(
            "SELECT t FROM TherapistProfile t", TherapistProfile.class);
        return query.getResultList();
    }

    @Override
    public void delete(TherapistProfile therapistProfile) {
        if (entityManager.contains(therapistProfile)) {
            entityManager.remove(therapistProfile);
        } else {
            TherapistProfile managed = entityManager.merge(therapistProfile);
            entityManager.remove(managed);
        }
    }

    @Override
    public void deleteById(TherapistProfileId id) {
        Optional<TherapistProfile> profile = findById(id);
        profile.ifPresent(this::delete);
    }

    @Override
    public Optional<TherapistProfile> findByDocumentTypeAndNumber(String documentType, String documentNumber) {
        TypedQuery<TherapistProfile> query = entityManager.createQuery(
            "SELECT t FROM TherapistProfile t WHERE t.documentType = :documentType AND t.identityDocumentNumber = :documentNumber", 
            TherapistProfile.class);
        query.setParameter("documentType", documentType);
        query.setParameter("documentNumber", documentNumber);
        List<TherapistProfile> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<TherapistProfile> findBySpecialtyName(String specialtyName) {
        TypedQuery<TherapistProfile> query = entityManager.createQuery(
            "SELECT t FROM TherapistProfile t WHERE t.specialtyName = :specialtyName", 
            TherapistProfile.class);
        query.setParameter("specialtyName", specialtyName);
        return query.getResultList();
    }
}