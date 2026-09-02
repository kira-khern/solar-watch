package com.codecool.solarwatch.dto.suntimes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public record SunriseSunsetApiResponse(
        SunriseSunsetResult results,
        String status
) {
}
