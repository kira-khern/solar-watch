package com.codecool.solarwatch.service;

import com.codecool.solarwatch.dto.suntimes.SunTimesAdminRequest;
import com.codecool.solarwatch.dto.suntimes.SunTimesAdminResponse;
import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SunriseSunsetTimes;
import com.codecool.solarwatch.repository.CityRepository;
import com.codecool.solarwatch.repository.SunTimesRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class SunTimesAdminService {

    private final SunTimesRepository sunTimesRepository;
    private final CityRepository cityRepository;

    public SunTimesAdminService(SunTimesRepository sunTimesRepository, CityRepository cityRepository) {
        this.sunTimesRepository = sunTimesRepository;
        this.cityRepository = cityRepository;
    }

    public SunTimesAdminResponse create(SunTimesAdminRequest request) {
        City city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> new CityNotFoundException("id " + request.cityId()));

        SunriseSunsetTimes entry = new SunriseSunsetTimes(
                city, request.date(), request.sunrise(), request.sunset());
        SunriseSunsetTimes saved = sunTimesRepository.save(entry);
        return toResponse(saved);
    }

    public SunTimesAdminResponse update(Long id, SunTimesAdminRequest request) {
        SunriseSunsetTimes existing = sunTimesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sun times entry not found: " + id));

        existing.setSunrise(request.sunrise());
        existing.setSunset(request.sunset());
        SunriseSunsetTimes saved = sunTimesRepository.save(existing);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!sunTimesRepository.existsById(id)) {
            throw new NoSuchElementException("Sun times entry not found: " + id);
        }
        sunTimesRepository.deleteById(id);
    }

    private SunTimesAdminResponse toResponse(SunriseSunsetTimes entry) {
        return new SunTimesAdminResponse(
                entry.getId(),
                entry.getCity().getId(),
                entry.getCity().getName(),
                entry.getDate(),
                entry.getSunrise(),
                entry.getSunset()
        );
    }
}