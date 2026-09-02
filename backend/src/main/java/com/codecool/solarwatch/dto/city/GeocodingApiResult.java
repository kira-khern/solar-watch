package com.codecool.solarwatch.dto.city;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeocodingApiResult(
        String name,
        double lat,
        double lon,
        String country,
        String state
) {
}
