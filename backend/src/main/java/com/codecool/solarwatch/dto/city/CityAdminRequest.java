package com.codecool.solarwatch.dto.city;

public record CityAdminRequest(
        String name,
        String state,
        String country,
        double latitude,
        double longitude
) {
}