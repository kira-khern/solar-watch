package com.codecool.solarwatch.service;

import com.codecool.solarwatch.dto.city.CityCoordinates;
import com.codecool.solarwatch.dto.city.GeocodingApiResult;
import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.repository.CityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GeocodingService {

    private final CityRepository cityRepository;
    private final WebClient geocodingWebClient;
    private final String apiKey;

    public GeocodingService(
            CityRepository cityRepository,
            WebClient geocodingWebClient,
            @Value("${openweather.api-key}") String apiKey) {
        this.cityRepository = cityRepository;
        this.geocodingWebClient = geocodingWebClient;
        this.apiKey = apiKey;
    }

    public City getCity(String cityName) {
        return cityRepository.findByName(cityName)
                .orElseGet(() -> fetchAndSaveCity(cityName));
    }

    public CityCoordinates getCoordinates(String cityName) {
        City city = getCity(cityName);
        return new CityCoordinates(city.getName(), city.getCountry(), city.getLatitude(), city.getLongitude());
    }

    private City fetchAndSaveCity(String cityName) {
        List<GeocodingApiResult> results = geocodingWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geo/1.0/direct")
                        .queryParam("q", cityName)
                        .queryParam("limit", 1)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GeocodingApiResult>>() {
                })
                .block();

        if (results == null || results.isEmpty()) {
            throw new CityNotFoundException(cityName);
        }

        GeocodingApiResult result = results.get(0);
        City newCity = new City(null, result.name(), result.country(), result.lat(), result.lon(), result.state());
        return cityRepository.save(newCity);
    }
}