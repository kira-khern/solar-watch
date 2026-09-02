package com.codecool.solarwatch.dto.suntimes;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record SunTimesAdminResponse(
        Long id,
        Long cityId,
        String cityName,
        LocalDate date,
        OffsetDateTime sunrise,
        OffsetDateTime sunset
) {
}