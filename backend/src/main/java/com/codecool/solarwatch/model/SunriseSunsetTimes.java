package com.codecool.solarwatch.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"city_id", "date"}))
public class SunriseSunsetTimes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    private LocalDate date;
    private OffsetDateTime sunrise;
    private OffsetDateTime sunset;

    protected SunriseSunsetTimes() {
    }

    public SunriseSunsetTimes(City city, LocalDate date, OffsetDateTime sunrise, OffsetDateTime sunset) {
        this.city = city;
        this.date = date;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public Long getId() {
        return id;
    }

    public City getCity() {
        return city;
    }

    public LocalDate getDate() {
        return date;
    }

    public OffsetDateTime getSunrise() {
        return sunrise;
    }

    public OffsetDateTime getSunset() {
        return sunset;
    }

    public void setSunrise(OffsetDateTime sunrise) {
        this.sunrise = sunrise;
    }

    public void setSunset(OffsetDateTime sunset) {
        this.sunset = sunset;
    }
}