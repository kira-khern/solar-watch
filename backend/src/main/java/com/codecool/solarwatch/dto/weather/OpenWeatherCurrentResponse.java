package com.codecool.solarwatch.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenWeatherCurrentResponse(
        List<WeatherCondition> weather,
        Main main,
        Wind wind
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WeatherCondition(
            String description,
            String icon
    ){
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Main(
            double temp,
            double feels_like,
            int humidity
    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wind(
            double speed
    ){}
}
