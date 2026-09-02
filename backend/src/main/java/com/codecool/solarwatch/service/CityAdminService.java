package com.codecool.solarwatch.service;

import com.codecool.solarwatch.dto.city.CityAdminRequest;
import com.codecool.solarwatch.dto.city.CityAdminResponse;
import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.repository.CityRepository;
import org.springframework.stereotype.Service;

@Service
public class CityAdminService {

    private final CityRepository cityRepository;

    public CityAdminService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public CityAdminResponse createCity(CityAdminRequest request) {
        City city = new City(null, request.name(), request.country(),
                request.latitude(), request.longitude(), request.state());
        City saved = cityRepository.save(city);
        return toResponse(saved);
    }

    public CityAdminResponse updateCity(Long id, CityAdminRequest request) {
        City existing = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("id " + id));

        City updated = new City(existing.getId(), request.name(), request.country(),
                request.latitude(), request.longitude(), request.state());
        City saved = cityRepository.save(updated);
        return toResponse(saved);
    }

    public void deleteCity(Long id) {
        if (!cityRepository.existsById(id)) {
            throw new CityNotFoundException("id " + id);
        }
        cityRepository.deleteById(id);
    }

    private CityAdminResponse toResponse(City city) {
        return new CityAdminResponse(
                city.getId(), city.getName(), city.getState(),
                city.getCountry(), city.getLatitude(), city.getLongitude());
    }
}