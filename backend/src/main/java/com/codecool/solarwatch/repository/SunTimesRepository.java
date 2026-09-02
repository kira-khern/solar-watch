package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SunriseSunsetTimes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SunTimesRepository extends JpaRepository<SunriseSunsetTimes, Long> {

    Optional<SunriseSunsetTimes> findByCityAndDate(City city, LocalDate date);
}