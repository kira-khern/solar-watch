package com.codecool.solarwatch.dto.weather;

public record WeatherInfo(
        double temperatureCelsius,
        double feelsLikeCelsius,
        String description,
        String icon,
        int humidityPercent,
        double windSpeedMps
) {
}
