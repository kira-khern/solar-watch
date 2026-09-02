package com.codecool.solarwatch.dto.suntimes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SunriseSunsetResult(
        String sunrise,
        String sunset
) {
}
