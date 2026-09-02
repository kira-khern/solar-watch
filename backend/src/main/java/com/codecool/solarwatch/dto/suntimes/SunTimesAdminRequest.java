package com.codecool.solarwatch.dto.suntimes;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record SunTimesAdminRequest(
        Long cityId,
        LocalDate date,
        OffsetDateTime sunrise,
        OffsetDateTime sunset
) {
}