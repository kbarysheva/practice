package com.tinkoff.practice;

public class CountryDto {
    private String countryName;
    private Region region;

    public CountryDto() {
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
