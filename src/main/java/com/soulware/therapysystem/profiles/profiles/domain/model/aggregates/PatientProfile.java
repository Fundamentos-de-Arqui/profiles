package com.soulware.therapysystem.profiles.profiles.domain.model.aggregates;

import com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "patient_profiles")
public class PatientProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    // Identity fields
    @Column(name = "first_names", nullable = false)
    private String firstNames;
    
    @Column(name = "paternal_surname", nullable = false)
    private String paternalSurname;
    
    @Column(name = "maternal_surname")
    private String maternalSurname;
    
    @Column(name = "identity_document_number", nullable = false, unique = true)
    private String identityDocumentNumber;
    
    @Column(name = "document_type", nullable = false)
    private String documentType;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    // Birth data fields
    @Column(name = "birth_place", nullable = false)
    private String birthPlace;
    
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    
    // Age fields
    @Column(name = "first_appointment_age", nullable = false)
    private Integer firstAppointmentAge;
    
    @Column(name = "current_age", nullable = false)
    private Integer currentAge;
    
    // Other fields
    @Column(name = "gender", nullable = false)
    private String gender;
    
    @Column(name = "marital_status", nullable = false)
    private String maritalStatus;
    
    @Column(name = "current_address", nullable = false)
    private String currentAddress;
    
    @Column(name = "district")
    private String district;
    
    @Column(name = "province")
    private String province;
    
    @Column(name = "region")
    private String region;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "religion")
    private String religion;
    
    @Column(name = "education_level")
    private String educationLevel;
    
    @Column(name = "occupation")
    private String occupation;
    
    @Column(name = "current_educational_institution")
    private String currentEducationalInstitution;

    // Referred therapist field
    @Column(name = "referred_therapist_name")
    private String referredTherapistName;

    // JPA default constructor
    protected PatientProfile() {}

    // Constructor sin ID para nuevas entidades (ID generado por DB)
    public PatientProfile(Identity identity, 
                         BirthData birthData, 
                         Age age, 
                         Gender gender, 
                         MaritalStatus maritalStatus, 
                         Address address, 
                         Religion religion, 
                         EducationData educationData,
                         ReferredTherapist referredTherapist) {
        if (identity == null) {
            throw new IllegalArgumentException("Identity cannot be null");
        }
        if (birthData == null) {
            throw new IllegalArgumentException("Birth data cannot be null");
        }
        if (age == null) {
            throw new IllegalArgumentException("Age cannot be null");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        if (maritalStatus == null) {
            throw new IllegalArgumentException("Marital status cannot be null");
        }
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (religion == null) {
            throw new IllegalArgumentException("Religion cannot be null");
        }
        if (educationData == null) {
            throw new IllegalArgumentException("Education data cannot be null");
        }
        // ReferredTherapist puede ser null (opcional)

        this.id = null; // Será generado por la base de datos
        this.firstNames = identity.firstNames().value();
        this.paternalSurname = identity.paternalSurname().value();
        this.maternalSurname = identity.maternalSurname() != null ? identity.maternalSurname().value() : null;
        this.identityDocumentNumber = identity.identityDocumentNumber().value();
        this.documentType = identity.documentType().value();
        this.phone = identity.phone().value();
        this.email = identity.email().value();
        this.birthPlace = birthData.birthPlace();
        this.birthDate = birthData.birthDate();
        this.firstAppointmentAge = age.firstAppointment();
        this.currentAge = age.current();
        this.gender = gender.value();
        this.maritalStatus = maritalStatus.value();
        this.currentAddress = address.currentAddress();
        this.district = address.district();
        this.province = address.province();
        this.region = address.region();
        this.country = address.country();
        this.religion = religion.value();
        this.educationLevel = educationData.educationLevel();
        this.occupation = educationData.occupation();
        this.currentEducationalInstitution = educationData.currentEducationalInstitution();
        
        // Referred therapist assignment (optional)
        this.referredTherapistName = referredTherapist != null ? referredTherapist.therapistName() : null;
    }

    // Método de negocio: actualizar edad actual
    public void updateCurrentAge(int newCurrentAge) {
        if (newCurrentAge < this.currentAge) {
            throw new IllegalArgumentException("New current age cannot be less than previous current age");
        }
        this.currentAge = newCurrentAge;
    }

    // Método de negocio: cambiar dirección
    public void changeAddress(Address newAddress) {
        if (newAddress == null) {
            throw new IllegalArgumentException("New address cannot be null");
        }
        this.currentAddress = newAddress.currentAddress();
        this.district = newAddress.district();
        this.province = newAddress.province();
        this.region = newAddress.region();
        this.country = newAddress.country();
    }

    // Método de negocio: actualizar estado civil
    public void updateMaritalStatus(MaritalStatus newMaritalStatus) {
        if (newMaritalStatus == null) {
            throw new IllegalArgumentException("New marital status cannot be null");
        }
        this.maritalStatus = newMaritalStatus.value();
    }

    // Método de negocio: actualizar religión
    public void updateReligion(Religion newReligion) {
        if (newReligion == null) {
            throw new IllegalArgumentException("New religion cannot be null");
        }
        this.religion = newReligion.value();
    }

    // Método de negocio: actualizar datos de educación
    public void updateEducationData(EducationData newEducationData) {
        if (newEducationData == null) {
            throw new IllegalArgumentException("New education data cannot be null");
        }
        this.educationLevel = newEducationData.educationLevel();
        this.occupation = newEducationData.occupation();
        this.currentEducationalInstitution = newEducationData.currentEducationalInstitution();
    }

    public void updateReferredTherapist(ReferredTherapist referredTherapist) {
        this.referredTherapistName = referredTherapist != null ? referredTherapist.therapistName() : null;
    }

    // Getters - Return value objects constructed from JPA fields
    public PatientProfileId getId() {
        return this.id != null ? new PatientProfileId(this.id) : null;
    }

    public Identity getIdentity() {
        return new Identity(
            new FirstNames(this.firstNames),
            new PaternalSurname(this.paternalSurname),
            this.maternalSurname != null ? new MaternalSurname(this.maternalSurname) : null,
            new IdentityDocumentNumber(this.identityDocumentNumber),
            new DocumentType(this.documentType),
            new Phone(this.phone),
            new Email(this.email)
        );
    }

    public BirthData getBirthData() {
        return new BirthData(this.birthPlace, this.birthDate);
    }

    public Age getAge() {
        return new Age(this.firstAppointmentAge, this.currentAge);
    }

    public Gender getGender() {
        return new Gender(this.gender);
    }

    public MaritalStatus getMaritalStatus() {
        return new MaritalStatus(this.maritalStatus);
    }

    public Address getAddress() {
        return new Address(
            this.currentAddress,
            this.district,
            this.province,
            this.region,
            this.country
        );
    }

    public Religion getReligion() {
        return new Religion(this.religion);
    }

    public EducationData getEducationData() {
        return new EducationData(
            this.educationLevel,
            this.occupation,
            this.currentEducationalInstitution
        );
    }

    public ReferredTherapist getReferredTherapist() {
        return this.referredTherapistName != null ? new ReferredTherapist(this.referredTherapistName) : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PatientProfile that = (PatientProfile) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PatientProfile{" +
                "id=" + id +
                ", firstNames='" + firstNames + '\'' +
                ", paternalSurname='" + paternalSurname + '\'' +
                ", currentAge=" + currentAge +
                '}';
    }
}