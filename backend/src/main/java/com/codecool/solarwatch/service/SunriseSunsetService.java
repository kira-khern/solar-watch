package com.codecool.solarwatch.service;

import com.codecool.solarwatch.dto.suntimes.SunTimes;
import com.codecool.solarwatch.dto.suntimes.SunriseSunsetApiResponse;
import com.codecool.solarwatch.exception.ExternalApiException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SunriseSunsetTimes;
import com.codecool.solarwatch.repository.SunTimesRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
public class SunriseSunsetService {

    private final SunTimesRepository sunTimesRepository;
    private final WebClient sunriseSunsetWebClient;

    public SunriseSunsetService(
            SunTimesRepository sunTimesRepository,
            @Qualifier("sunriseSunsetWebClient") WebClient sunriseSunsetWebClient) {
        this.sunTimesRepository = sunTimesRepository;
        this.sunriseSunsetWebClient = sunriseSunsetWebClient;
    }

    public SunTimes getSunTimes(City city, LocalDate date) {
        SunriseSunsetTimes times = sunTimesRepository.findByCityAndDate(city, date)
                .orElseGet(() -> fetchAndSaveSunTimes(city, date));

        return new SunTimes(times.getSunrise(), times.getSunset());
    }

    private SunriseSunsetTimes fetchAndSaveSunTimes(City city, LocalDate date) {
        SunriseSunsetApiResponse response = sunriseSunsetWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/json")
                        .queryParam("lat", city.getLatitude())
                        .queryParam("lng", city.getLongitude())
                        .queryParam("date", date)
                        .queryParam("formatted", 0)
                        .build())
                .retrieve()
                .bodyToMono(SunriseSunsetApiResponse.class)
                .block();

        if (response == null || response.results() == null || !"OK".equals(response.status())) {
            String status = response == null ? "null response" : response.status();
            throw new ExternalApiException("Sunrise/sunset service returned an unexpected status: " + status);
        }

        OffsetDateTime sunrise = OffsetDateTime.parse(response.results().sunrise());
        OffsetDateTime sunset = OffsetDateTime.parse(response.results().sunset());

        SunriseSunsetTimes newTimes = new SunriseSunsetTimes(city, date, sunrise, sunset);
        return sunTimesRepository.save(newTimes);
    }
}