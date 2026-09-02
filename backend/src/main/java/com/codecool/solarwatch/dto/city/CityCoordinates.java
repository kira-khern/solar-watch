package com.codecool.solarwatch.dto.city;

public record CityCoordinates(
        String cityName,
        String country,
        double lat,
        double lon
) {
}
