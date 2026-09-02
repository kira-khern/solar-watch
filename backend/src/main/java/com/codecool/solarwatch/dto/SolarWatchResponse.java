package com.codecool.solarwatch.dto;


public record SolarWatchResponse(
        String city,
        String country,
        String date,
        String sunriseUtc,
        String sunsetUtc,
        String sunriseLocal,
        String sunsetLocal
) {
}
