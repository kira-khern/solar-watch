package com.codecool.solarwatch.dto.city;

public record CityAdminResponse(
        Long id,
        String name,
        String state,
        String country,
        double latitude,
        double longitude
) {
}