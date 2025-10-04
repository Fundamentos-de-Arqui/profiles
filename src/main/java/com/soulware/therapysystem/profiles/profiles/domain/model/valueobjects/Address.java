package com.soulware.therapysystem.profiles.profiles.domain.model.valueobjects;

import java.util.Objects;

public final class Address {
    private final String currentAddress;
    private final String district;
    private final String province;
    private final String region;
    private final String country;

    public Address(String currentAddress, String district, String province, String region, String country) {
        if (currentAddress == null || currentAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Current address cannot be null or empty");
        }
        if (district == null || district.trim().isEmpty()) {
            throw new IllegalArgumentException("District cannot be null or empty");
        }
        if (province == null || province.trim().isEmpty()) {
            throw new IllegalArgumentException("Province cannot be null or empty");
        }
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("Region cannot be null or empty");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }
        
        this.currentAddress = currentAddress.trim();
        this.district = district.trim();
        this.province = province.trim();
        this.region = region.trim();
        this.country = country.trim();
    }

    public String currentAddress() {
        return currentAddress;
    }

    public String district() {
        return district;
    }

    public String province() {
        return province;
    }

    public String region() {
        return region;
    }

    public String country() {
        return country;
    }

    public String fullAddress() {
        return String.format("%s, %s, %s, %s, %s", 
            currentAddress, district, province, region, country);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Address address = (Address) obj;
        return Objects.equals(currentAddress, address.currentAddress) &&
               Objects.equals(district, address.district) &&
               Objects.equals(province, address.province) &&
               Objects.equals(region, address.region) &&
               Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentAddress, district, province, region, country);
    }

    @Override
    public String toString() {
        return fullAddress();
    }
}