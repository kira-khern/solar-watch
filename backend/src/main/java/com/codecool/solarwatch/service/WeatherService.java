package com.codecool.solarwatch.service;

import com.codecool.solarwatch.dto.weather.OpenWeatherCurrentResponse;
import com.codecool.solarwatch.dto.weather.WeatherInfo;
import com.codecool.solarwatch.exception.ExternalApiException;
import com.codecool.solarwatch.model.City;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WeatherService {
    private final WebClient geocodingWebClient;
    private final String apiKey;

    public WeatherService(WebClient geocodingWebClient, @Value("${openweather.api-key}") String apiKey){
        this.geocodingWebClient = geocodingWebClient;
        this.apiKey = apiKey;
    }

    public WeatherInfo getCurrentWeatherInfo(City city){
        OpenWeatherCurrentResponse response = geocodingWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/weather")
                        .queryParam("lat", city.getLatitude())
                        .queryParam("lon", city.getLongitude())
                        .queryParam("units", "metric")
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OpenWeatherCurrentResponse.class)
                .block();

        if (response == null || response.weather() == null || response.weather().isEmpty() || response.main() == null) {
            throw new ExternalApiException("Weather data unavailable for city: " + city.getName());
        }

        OpenWeatherCurrentResponse.WeatherCondition condition = response.weather().get(0);
        double windSpeed = response.wind() != null ? response.wind().speed() : 0.0;

        return new WeatherInfo(
                response.main().temp(),
                response.main().feels_like(),
                condition.description(),
                condition.icon(),
                response.main().humidity(),
                windSpeed
        );
    }


}
