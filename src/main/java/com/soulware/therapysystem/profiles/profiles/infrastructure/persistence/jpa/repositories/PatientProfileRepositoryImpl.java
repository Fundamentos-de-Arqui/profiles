package com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.repositories;

import com.soulware.therapysystem.profiles.profiles.domain.model.aggregates.PatientProfile;
import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.PatientProfileId;
import com.soulware.therapysystem.profiles.profiles.infrastructure.persistence.jpa.PatientProfileRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PatientProfileRepositoryImpl implements PatientProfileRepository {

    @PersistenceContext(unitName = "profilesPU")
    private EntityManager entityManager;

    @Override
    public PatientProfile save(PatientProfile patientProfile) {
        PatientProfileId profileId = patientProfile.getId();
        if (profileId == null) {
            // Nueva entidad - usar persist
            entityManager.persist(patientProfile);
            return patientProfile;
        } else {
            // Entidad existente - usar merge
            return entityManager.merge(patientProfile);
        }
    }

    @Override
    public Optional<PatientProfile> findById(PatientProfileId id) {
        PatientProfile patientProfile = entityManager.find(PatientProfile.class, id.value());
        return Optional.ofNullable(patientProfile);
    }

    @Override
    public List<PatientProfile> findAll() {
        TypedQuery<PatientProfile> query = entityManager.createQuery(
            "SELECT p FROM PatientProfile p", PatientProfile.class);
        return query.getResultList();
    }

    @Override
    public void delete(PatientProfile patientProfile) {
        if (entityManager.contains(patientProfile)) {
            entityManager.remove(patientProfile);
        } else {
            // Entity is detached, merge first then remove
            PatientProfile managed = entityManager.merge(patientProfile);
            entityManager.remove(managed);
        }
    }

    @Override
    public void deleteById(PatientProfileId id) {
        Optional<PatientProfile> patientProfile = findById(id);
        patientProfile.ifPresent(this::delete);
    }

    @Override
    public Optional<PatientProfile> findByDocumentTypeAndNumber(String documentType, String documentNumber) {
        TypedQuery<PatientProfile> query = entityManager.createQuery(
            "SELECT p FROM PatientProfile p WHERE p.documentType = :documentType AND p.identityDocumentNumber = :documentNumber", 
            PatientProfile.class);
        query.setParameter("documentType", documentType);
        query.setParameter("documentNumber", documentNumber);
        List<PatientProfile> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}