package com.tinkoff.practice;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator")
    @SequenceGenerator(name = "sequence_generator")
    private int id;
    private String region_name;

    public Region() {
    }

    public Region(String region_name) {
        this.region_name = region_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return id == region.id && Objects.equals(region_name, region.region_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, region_name);
    }
}

