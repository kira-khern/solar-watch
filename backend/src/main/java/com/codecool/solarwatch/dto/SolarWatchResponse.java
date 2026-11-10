package com.codecool.solarwatch.dto;


import com.codecool.solarwatch.dto.weather.WeatherInfo;

public record SolarWatchResponse(
        String city,
        String country,
        String date,
        String sunriseUtc,
        String sunsetUtc,
        String sunriseLocal,
        String sunsetLocal,
        WeatherInfo weather,
        double latitude,
        double longitude

) {
}
