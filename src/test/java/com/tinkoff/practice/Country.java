package com.tinkoff.practice;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator")
    @SequenceGenerator(name = "sequence_generator")

    private int id;
    private String country_name;
    private int region_id;
    public Country() {
    }

    public Country(String country_name, int region_id) {
        this.country_name = country_name;
        this.region_id = region_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return id == country.id && region_id == country.region_id && Objects.equals(country_name, country.country_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, country_name, region_id);
    }
}
